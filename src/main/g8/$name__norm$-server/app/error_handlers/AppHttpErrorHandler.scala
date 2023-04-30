package error_handlers

import config.AppServerConfig

import javax.inject._
import play.api._
import play.api.http.{DefaultHttpErrorHandler, Status}
import play.api.mvc._
import play.api.routing.Router

import scala.concurrent._

@Singleton
class AppHttpErrorHandler @Inject() (
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router],
    appServerConfig: AppServerConfig
) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    if (request.queryString.contains(appServerConfig.ui.spaRedirectRouteQueryParam)) super.onNotFound(request, message)
    else
      Future.successful(
        Results.Redirect(
          url = appServerConfig.ui.spaRedirectUrl,
          queryStringParams = Map(appServerConfig.ui.spaRedirectRouteQueryParam -> Seq(request.uri)),
          status = Status.FOUND
        )
      )
  }
}
