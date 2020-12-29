package config

import com.typesafe.config.Config
import play.api.ConfigLoader
import AppConfig.Paths._

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object AppConfig {

  object Paths {
    val Root                                      = "app"
    val AuthClientName                            = "auth.client-name"
    val AuthCallbackUrl                           = "auth.callback-url"
    val AuthDefaultLoginUrl                       = "auth.default-login-url"
    val AuthDefaultLogoutUrl                      = "auth.default-logout-url"
    val SubscriptionsTransportWsKeepAliveInterval = "graphql.subscriptions-transport-ws.keep-alive-interval"
    val SubscriptionsTransportWsKeepAliveIntervalTimeUnit =
      "graphql.subscriptions-transport-ws.keep-alive-interval-time-unit"
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
      graphql = GraphQLConfig(
        subscriptionsTransportWsKeepAliveInterval = FiniteDuration(
          config.getLong(SubscriptionsTransportWsKeepAliveInterval),
          config.getString(SubscriptionsTransportWsKeepAliveIntervalTimeUnit)
        )
      )
    )
  }
}

case class AppConfig(auth: AuthConfig, graphql: GraphQLConfig)
