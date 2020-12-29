package graphql.apollo

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import graphql.apollo.SubscriptionsTransportWsConnection.{Disconnect, KeepAlive, PayloadData, Protocol}
import graphql.{GraphQLConstants, GraphQLContextFactory}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.AnyContent

import scala.concurrent.duration.FiniteDuration

object SubscriptionsTransportWsConnection {

  sealed trait Protocol
  case class PayloadData(text: String) extends Protocol
  case object Disconnect               extends Protocol
  case object KeepAlive                extends Protocol

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

  def sessionInit: Behavior[Protocol] = Behaviors.receiveMessage {
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
    case Disconnect => Behaviors.stopped // TODO: Add cleanup logic
    case _          => Behaviors.same    // TODO: Perhaps log that there is some unexpected message
  }

  def connected(connectionInit: ConnectionInit): Behavior[Protocol] = {
    out ! Json.toJson(ConnectionAck()).toString
    out ! Json.toJson(ConnectionKeepAlive.Instance).toString
    timers.startTimerWithFixedDelay(KeepAlive, keepAliveInterval)
    Behaviors.receiveMessage {
      case KeepAlive =>
        out ! Json.toJson(ConnectionKeepAlive.Instance).toString
        Behaviors.same

      case _ => Behaviors.same
    }
  }
}
