package config

import scala.concurrent.duration.FiniteDuration

case class GraphQLConfig(subscriptionsTransportWsKeepAliveInterval: FiniteDuration)
