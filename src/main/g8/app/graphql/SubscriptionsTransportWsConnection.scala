package graphql

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import graphql.SubscriptionsTransportWsConnection.Protocol
import org.pac4j.core.profile.CommonProfile
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import org.pac4j.play.scala.AuthenticatedRequest

object SubscriptionsTransportWsConnection {

  sealed trait Protocol
  case class PayloadData(text: JsValue) extends Protocol
  case object Disconnect                extends Protocol

  def apply(
      out: ActorRef[JsValue],
      request: AuthenticatedRequest[CommonProfile, AnyContent],
      graphQLContextFactory: GraphQLContextFactory
  ): Behavior[Protocol] = Behaviors.setup { context => new SubscriptionsTransportWsConnection(context) }
}

class SubscriptionsTransportWsConnection(context: ActorContext[Protocol]) extends AbstractBehavior[Protocol](context) {

  override def onMessage(msg: Protocol): Behavior[Protocol] = msg match {
    case _ => println(msg); this
  }
}
