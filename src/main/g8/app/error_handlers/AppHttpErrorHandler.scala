package error_handlers

import config.AppConfig

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
    appConfig: AppConfig
) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    if (request.queryString.contains(appConfig.ui.spaRedirectRouteQueryParam)) super.onNotFound(request, message)
    else
      Future.successful(
        Results.Redirect(
          url = "/",
          queryStringParams = Map(appConfig.ui.spaRedirectRouteQueryParam -> Seq(request.uri)),
          status = Status.FOUND
        )
      )
  }
}
