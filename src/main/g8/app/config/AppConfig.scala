package config

import com.typesafe.config.Config
import config.AppConfig.Paths._
import play.api.ConfigLoader

object AppConfig {

  object Paths {
    val Root                                      = "app"
    val AuthClientName                            = "auth.client-name"
    val AuthCallbackUrl                           = "auth.callback-url"
    val AuthDefaultLoginUrl                       = "auth.default-login-url"
    val AuthDefaultLogoutUrl                      = "auth.default-logout-url"
  }

  implicit val configLoader: ConfigLoader[AppConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)

    AppConfig(
      auth = AuthConfig(
        clientName = config.getString(AuthClientName),
        callbackUrl = config.getString(AuthCallbackUrl),
        defaultLoginUrl = config.getString(AuthDefaultLoginUrl),
        defaultLogoutUrl = config.getString(AuthDefaultLogoutUrl)
      )
    )
  }
}

case class AppConfig(auth: AuthConfig)
