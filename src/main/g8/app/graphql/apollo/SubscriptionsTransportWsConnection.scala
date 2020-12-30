package graphql.apollo

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import akka.stream.{KillSwitches, Materializer}
import akka.stream.scaladsl.{Keep, Sink}
import graphql.apollo.SubscriptionsTransportWsConnection.{Command, Disconnect, KeepAlive, PayloadData}
import graphql.{ErrorMessages, GraphQLConstants, GraphQLContextFactory, GraphQLQueryExecution}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.AnyContent
import sangria.ast.OperationType.{Mutation, Query, Subscription}
import sangria.execution.{ErrorWithResolver, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}
import sangria.execution.ExecutionScheme.Stream
import sangria.streaming.akkaStreams._

object SubscriptionsTransportWsConnection {

  sealed trait Command
  case class PayloadData(text: String)                   extends Command
  case class Disconnect(error: Option[Throwable] = None) extends Command
  case object KeepAlive                                  extends Command

  def apply(
      out: ActorRef[String],
      request: AuthenticatedRequest[CommonProfile, AnyContent],
      keepAliveInterval: FiniteDuration
  )(implicit ec: ExecutionContext, mat: Materializer, graphQLContextFactory: GraphQLContextFactory): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.withTimers { timers =>
        new SubscriptionsTransportWsConnection(out, request, keepAliveInterval, context, timers).sessionInit
      }
    }
}

class SubscriptionsTransportWsConnection(
    out: ActorRef[String],
    request: AuthenticatedRequest[CommonProfile, AnyContent],
    keepAliveInterval: FiniteDuration,
    context: ActorContext[Command],
    timers: TimerScheduler[Command]
)(implicit ec: ExecutionContext, mat: Materializer, graphQLContextFactory: GraphQLContextFactory)
    extends GraphQLQueryExecution {

  def sessionInit: Behavior[Command] =
    Behaviors
      .receiveMessage(sessionInitPartial.orElse(handleDisconnectCommand).orElse(handleUnexpectedCommand))
      .receiveSignal(handlePostStopSignal)

  private val sessionInitPartial: PartialFunction[Command, Behavior[Command]] = {
    case PayloadData(text) =>
      Json.parse(text).validate[OperationMessage] match {
        case JsSuccess(init: ConnectionInit, _)   => connected(init)
        case JsSuccess(_: ConnectionTerminate, _) => Behaviors.stopped
        case JsSuccess(_, _)                      => sendConnectionError("Invalid operation during session init phase"); Behaviors.same
        case _: JsError                           => sendConnectionError("Invalid message"); Behaviors.same
      }
  }

  def connected(connectionInit: ConnectionInit): Behavior[Command] = {
    out ! Json.toJson(ConnectionAck()).toString
    out ! Json.toJson(ConnectionKeepAlive.Instance).toString
    timers.startTimerWithFixedDelay(KeepAlive, keepAliveInterval)
    Behaviors
      .receiveMessage[Command](connectedPartial.orElse(handleDisconnectCommand).orElse(handleUnexpectedCommand))
      .receiveSignal(handlePostStopSignal)
  }

  private val connectedPartial: PartialFunction[Command, Behavior[Command]] = {
    case PayloadData(text) =>
      Json.parse(text).validate[OperationMessage] match {
        case JsSuccess(_: ConnectionTerminate, _) => Behaviors.stopped
        case JsSuccess(start: Start, _)           => startSubscription(start); Behaviors.same
        case JsSuccess(stop: Stop, _)             => stopSubscription(stop); Behaviors.same
        case JsSuccess(_, _)                      => sendConnectionError("Invalid operation during connected phase"); Behaviors.same
        case _: JsError                           => sendConnectionError("Invalid message"); Behaviors.same
      }
    case KeepAlive =>
      out ! Json.toJson(ConnectionKeepAlive.Instance).toString
      Behaviors.same
  }

  private val handleDisconnectCommand: PartialFunction[Command, Behavior[Command]] = {
    case Disconnect(error) =>
      error.foreach(e => context.log.error("Connection failed", e))
      Behaviors.stopped
  }

  private val handleUnexpectedCommand: PartialFunction[Command, Behavior[Command]] = {
    case message =>
      context.log.warn("Unexpected message {}", message)
      Behaviors.same
  }

  private val handlePostStopSignal: PartialFunction[(ActorContext[Command], Signal), Behavior[Command]] = {
    case (_, PostStop) => close(); Behaviors.same
  }

  private def sendConnectionError(message: String): Unit = {
    out ! Json
      .toJson(
        ConnectionError(
          Json.obj(
            GraphQLConstants.ErrorFieldName.Message -> message
          )
        )
      )
      .toString
  }

  private def startSubscription(start: Start): Unit = {
    val (query, operation, variables) = extractQueryFields(start.payload)

    QueryParser.parse(query) match {
      case Success(queryAst) =>
        queryAst.operationType(operation) match {
          case Some(Subscription) | None =>
            val subscriptionSource: AkkaSource[JsValue] = executeStandardQuery(request, queryAst, variables, operation)
              .recover {
                case error: QueryAnalysisError => Json.toJson(Data(id = start.id, payload = error.resolveError))
                case error: ErrorWithResolver  => Json.toJson(Data(id = start.id, payload = error.resolveError))
                case e =>
                  Json.toJson(
                    Data(id = start.id, payload = Json.obj(GraphQLConstants.ErrorFieldName.Message -> e.getMessage))
                  )
              }

            val (killSwitch, status) = subscriptionSource
              .viaMat(KillSwitches.single)(Keep.right)
              .map(p => Json.toJson(Data(id = start.id, payload = p)))
              .toMat(Sink.foreach(out ! _.toString))(Keep.both)
              .run()

            status.onComplete(_ => out ! Json.toJson(Complete(id = start.id)).toString)
          case Some(Query) | Some(Mutation) =>
            out ! Json
              .toJson(
                Error(
                  id = start.id,
                  payload = Json.obj(
                    GraphQLConstants.ErrorFieldName.Message -> ErrorMessages.unsupportedOperationType(Subscription)
                  )
                )
              )
              .toString
        }
      case Failure(error: SyntaxError) =>
        out ! Json
          .toJson(
            Error(
              id = start.id,
              payload = Json.obj(
                GraphQLConstants.ErrorFieldName.Message -> error.getMessage(),
                GraphQLConstants.ErrorFieldName.Locations -> Json.arr(
                  Json.obj(
                    GraphQLConstants.ErrorFieldName.Line   -> error.originalError.position.line,
                    GraphQLConstants.ErrorFieldName.Column -> error.originalError.position.column
                  )
                )
              )
            )
          )
          .toString
      case Failure(exception) =>
        out ! Json
          .toJson(
            Error(id = start.id, payload = Json.obj(GraphQLConstants.ErrorFieldName.Message -> exception.getMessage()))
          )
          .toString
    }
  }

  private def stopSubscription(stop: Stop): Unit = {}

  private def close(): Unit = {}
}
