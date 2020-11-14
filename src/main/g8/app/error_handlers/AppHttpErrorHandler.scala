package error_handlers

import javax.inject._
import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc._
import play.api.routing.Router

import scala.concurrent._

@Singleton
class AppHttpErrorHandler @Inject() (
  env: Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: Provider[Router]
) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    super.onNotFound(request, message)
  }
}
