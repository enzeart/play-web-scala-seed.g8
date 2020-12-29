package graphql.apollo

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import graphql.apollo.SubscriptionsTransportWsConnection.{Disconnect, KeepAlive, PayloadData, Protocol}
import graphql.{GraphQLConstants, GraphQLContextFactory}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.AnyContent

import scala.concurrent.duration.FiniteDuration

object SubscriptionsTransportWsConnection {

  sealed trait Protocol
  case class PayloadData(text: String)                   extends Protocol
  case class Disconnect(error: Option[Throwable] = None) extends Protocol
  case object KeepAlive                                  extends Protocol

  def apply(
      out: ActorRef[String],
      request: AuthenticatedRequest[CommonProfile, AnyContent],
      keepAliveInterval: FiniteDuration
  )(implicit graphQLContextFactory: GraphQLContextFactory): Behavior[Protocol] =
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
    context: ActorContext[Protocol],
    timers: TimerScheduler[Protocol]
) {

  def sessionInit: Behavior[Protocol] =
    Behaviors.receiveMessage(sessionInitPartial.orElse(disconnect).orElse(unexpectedMessage)).receiveSignal(postStop)

  def connected(connectionInit: ConnectionInit): Behavior[Protocol] = {
    out ! Json.toJson(ConnectionAck()).toString
    out ! Json.toJson(ConnectionKeepAlive.Instance).toString
    timers.startTimerWithFixedDelay(KeepAlive, keepAliveInterval)
    Behaviors
      .receiveMessage[Protocol](connectedPartial.orElse(disconnect).orElse(unexpectedMessage))
      .receiveSignal(postStop)
  }

  private val sessionInitPartial: PartialFunction[Protocol, Behavior[Protocol]] = {
    case PayloadData(text) =>
      Json.parse(text).validate[OperationMessage] match {
        case JsSuccess(init: ConnectionInit, _) => connected(init)
        case JsSuccess(_, _) =>
          val connectionError = ConnectionError(
            Json.obj(
              GraphQLConstants.ErrorFieldName.Message -> "Invalid operation during session init phase"
            )
          )
          out ! Json.toJson(connectionError).toString
          Behaviors.same
        case _: JsError =>
          val connectionError = ConnectionError(
            Json.obj(
              GraphQLConstants.ErrorFieldName.Message -> "Invalid message"
            )
          )
          out ! Json.toJson(connectionError).toString
          Behaviors.same
      }
  }

  private val disconnect: PartialFunction[Protocol, Behavior[Protocol]] = {
    case Disconnect(error) =>
      error.foreach(e => context.log.error("Connection failed", e))
      Behaviors.stopped
  }

  private val unexpectedMessage: PartialFunction[Protocol, Behavior[Protocol]] = {
    case message =>
      context.log.warn("Unexpected message", message)
      Behaviors.same
  }

  private val postStop: PartialFunction[(ActorContext[Protocol], Signal), Behavior[Protocol]] = {
    case (_, PostStop) => close(); Behaviors.same
  }

  private val connectedPartial: PartialFunction[Protocol, Behavior[Protocol]] = {
    case KeepAlive =>
      out ! Json.toJson(ConnectionKeepAlive.Instance).toString
      Behaviors.same
  }

  def close(): Unit = {}
}
