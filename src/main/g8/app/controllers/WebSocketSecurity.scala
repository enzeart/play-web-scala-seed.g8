package controllers

import akka.stream.scaladsl.Flow
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.{AuthenticatedRequest, SecureAction, Security}
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{AnyContent, AnyContentAsEmpty, Result, WebSocket}
import org.pac4j.play.java.{SecureAction => SecureJavaAction}
import scala.jdk.CollectionConverters._
import scala.jdk.FutureConverters._

import scala.concurrent.{ExecutionContext, Future}

trait WebSocketSecurity[P <: CommonProfile] { self: Security[P] =>

  implicit class SecureWebSocket(secureAction: SecureAction[P, AnyContent, AuthenticatedRequest])(
      implicit ec: ExecutionContext
  ) {

    def webSocket[In, Out](
        f: AuthenticatedRequest[AnyContent] => Future[Either[Result, Flow[In, Out, _]]]
    )(implicit transformer: MessageFlowTransformer[In, Out]): WebSocket =
      WebSocket.acceptOrResult[In, Out] { request =>
        val webContext       = new PlayWebContext(request, playSessionStore)
        val secureJavaAction = new SecureJavaAction(config, playSessionStore)
        secureJavaAction
          .call(
            webContext,
            secureAction.clients,
            secureAction.authorizers,
            secureAction.matchers,
            secureAction.multiProfile
          )
          .asScala
          .map(Option(_))
          .flatMap {
            case Some(r) => Future successful Left(r.asScala)
            case None =>
              val profileManager = new ProfileManager[P](webContext)
              val profiles       = profileManager.getAllLikeDefaultSecurityLogic(true)
              f(
                AuthenticatedRequest(
                  profiles.asScala.toList,
                  webContext.supplementRequest(request.asJava).asScala.withBody(AnyContentAsEmpty)
                )
              )
          }
      }
  }
}
