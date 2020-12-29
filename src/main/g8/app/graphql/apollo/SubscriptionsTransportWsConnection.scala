package graphql.apollo

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import graphql.apollo.SubscriptionsTransportWsConnection.{Disconnect, KeepAlive, PayloadData, Command}
import graphql.{GraphQLConstants, GraphQLContextFactory}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.AnyContent

import scala.concurrent.duration.FiniteDuration

object SubscriptionsTransportWsConnection {

  sealed trait Command
  case class PayloadData(text: String)                   extends Command
  case class Disconnect(error: Option[Throwable] = None) extends Command
  case object KeepAlive                                  extends Command

  def apply(
      out: ActorRef[String],
      request: AuthenticatedRequest[CommonProfile, AnyContent],
      keepAliveInterval: FiniteDuration
  )(implicit graphQLContextFactory: GraphQLContextFactory): Behavior[Command] =
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
) {

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

  private def startSubscription(start: Start): Unit = {}

  private def stopSubscription(stop: Stop): Unit = {}

  private def close(): Unit = {}
}
