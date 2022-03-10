package controllers

import config.AppConfig
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.mvc.{Action, AnyContent, BaseController, Results}

import javax.inject.{Inject, Singleton}

@Singleton
class AuthController @Inject() (
    val controllerComponents: SecurityComponents,
    appConfig: AppConfig
) extends BaseController
    with Security[CommonProfile] {

  def login(): Action[AnyContent] = Secure(appConfig.auth.clientName) { request =>
    val queryStringParams = request
      .getQueryString(appConfig.ui.spaRedirectRouteQueryParam)
      .map(route => Map(appConfig.ui.spaRedirectRouteQueryParam -> Seq(route)))
      .getOrElse(Map.empty)

    Results.Redirect(
      url = appConfig.ui.spaRedirectUrl,
      queryStringParams = queryStringParams,
      status = FOUND
    )
  }
}
