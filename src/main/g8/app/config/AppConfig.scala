package config

import com.typesafe.config.Config
import config.AppConfig.Paths._
import play.api.ConfigLoader

object AppConfig {

  object Paths {
    val Root                         = "app"
    val AuthClientName               = "auth.client-name"
    val AuthCallbackUrl              = "auth.callback-url"
    val AuthDefaultLoginUrl          = "auth.default-login-url"
    val AuthDefaultLogoutUrl         = "auth.default-logout-url"
    val UiSpaRedirectRouteQueryParam = "ui.spa-redirect-route-query-param"
    val UiSpaRedirectUrl             = "ui.spa-redirect-url"
  }

  implicit val configLoader: ConfigLoader[AppConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)

    AppConfig(
      auth = AuthConfig(
        clientName = config.getString(AuthClientName),
        callbackUrl = config.getString(AuthCallbackUrl),
        defaultLoginUrl = config.getString(AuthDefaultLoginUrl),
        defaultLogoutUrl = config.getString(AuthDefaultLogoutUrl)
      ),
      ui = UiConfig(
        spaRedirectRouteQueryParam = config.getString(UiSpaRedirectRouteQueryParam),
        spaRedirectUrl = config.getString(UiSpaRedirectUrl)
      )
    )
  }
}

case class AppConfig(auth: AuthConfig, ui: UiConfig)
