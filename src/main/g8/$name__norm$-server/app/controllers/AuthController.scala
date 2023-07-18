package controllers

import config.AppServerConfig
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.mvc.{Action, AnyContent, BaseController, Results}

import javax.inject.{Inject, Singleton}

@Singleton
class AuthController @Inject() (
    val controllerComponents: SecurityComponents,
    appServerConfig: AppServerConfig
) extends BaseController
    with Security[CommonProfile] {

  def login(): Action[AnyContent] = Secure(appServerConfig.auth.clientName) { request =>
    val queryStringParams = request
      .getQueryString(appServerConfig.ui.appRedirectRouteQueryParam)
      .map(route => Map(appServerConfig.ui.appRedirectRouteQueryParam -> Seq(route)))
      .getOrElse(Map.empty)

    Results.Redirect(
      url = appServerConfig.ui.appRedirectUrl,
      queryStringParams = queryStringParams,
      status = FOUND
    )
  }
}
