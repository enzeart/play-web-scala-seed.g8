package controllers

import akka.stream.scaladsl.Flow
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.context.PlayFrameworkParameters
import org.pac4j.play.java.{SecureAction => SecureJavaAction}
import org.pac4j.play.result.PlayWebContextResultHolder
import org.pac4j.play.scala.{AuthenticatedRequest, SecureAction, Security}
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{AnyContent, AnyContentAsEmpty, Result, WebSocket}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.jdk.FutureConverters._

/** This class implements security for WebSocket endpoints based on the logic in
  * [[org.pac4j.play.scala.SecureAction.invokeBlock]]
  */
trait WebSocketSecurity[P <: UserProfile] { self: Security[P] =>

  implicit class SecureWebSocket(secureAction: SecureAction[P, AnyContent, AuthenticatedRequest])(implicit
      ec: ExecutionContext
  ) {

    def websocket[In, Out](
        f: AuthenticatedRequest[AnyContent] => Future[Either[Result, Flow[In, Out, _]]]
    )(implicit transformer: MessageFlowTransformer[In, Out]): WebSocket =
      WebSocket.acceptOrResult[In, Out] { request =>
        val secureJavaAction = new SecureJavaAction(config)
        val parameters = new PlayFrameworkParameters(request)
        secureJavaAction
          .call(parameters, secureAction.clients, secureAction.authorizers, secureAction.matchers)
          .asScala
          .flatMap {
            case holder: PlayWebContextResultHolder =>
              val webContext = holder.getPlayWebContext
              val sessionStore = config.getSessionStoreFactory.newSessionStore(parameters)
              val profileManager = config.getProfileManagerFactory.apply(webContext, sessionStore)
              val profiles = profileManager.getProfiles
              val sProfiles = profiles.asScala.toList.asInstanceOf[List[P]]
              val sRequest = webContext.supplementRequest(request.asJava).asScala.withBody(AnyContentAsEmpty)
              f(AuthenticatedRequest(sProfiles, sRequest))
            case r =>
              Future.successful(Left(r.asScala))
          }
      }

    def webSocketAccept[In, Out](
        f: AuthenticatedRequest[AnyContent] => Flow[In, Out, _]
    )(implicit transformer: MessageFlowTransformer[In, Out]): WebSocket = {
      websocket(request => Future(Right(f(request))))
    }
  }
}
