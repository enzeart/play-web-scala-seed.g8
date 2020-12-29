package graphql

import java.util.UUID

package object apollo {

  def randomSubscriptionsTransportWsConnectionName: String =
    s"subscriptions-transport-ws-connection-\${UUID.randomUUID()}"
}
