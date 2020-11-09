package config

import com.typesafe.config.Config
import play.api.ConfigLoader
import ConfigPaths._

object AppConfig {

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
