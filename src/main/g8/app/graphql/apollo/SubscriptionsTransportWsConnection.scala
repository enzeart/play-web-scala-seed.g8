package graphql.apollo

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import graphql.GraphQLContextFactory
import graphql.apollo.SubscriptionsTransportWsConnection.{Disconnect, PayloadData, Protocol}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest
import play.api.libs.json.Json
import play.api.mvc.AnyContent

object SubscriptionsTransportWsConnection {

  sealed trait Protocol
  case class PayloadData(text: String) extends Protocol
  case object Disconnect               extends Protocol

  def apply(
      out: ActorRef[String],
      request: AuthenticatedRequest[CommonProfile, AnyContent]
  )(implicit graphQLContextFactory: GraphQLContextFactory): Behavior[Protocol] = Behaviors.setup { context =>
    new SubscriptionsTransportWsConnection(context)
  }
}

class SubscriptionsTransportWsConnection(context: ActorContext[Protocol]) extends AbstractBehavior[Protocol](context) {

  override def onMessage(msg: Protocol): Behavior[Protocol] = msg match {
    case PayloadData(text) =>
//      val json = Json.parse(text).validate[OperationMessage]
      this
    case Disconnect => Behaviors.stopped
  }

}
