package graphql.apollo

import play.api.libs.json._

object MessageType extends Enumeration {

  val GqlConnectionInit: Value = Value("connection_init")

  val GqlConnectionError: Value = Value("connection_error")

  val GqlConnectionAck: Value = Value("connection_ack")

  val GqlConnectionKeepAlive: Value = Value("ka")

  implicit val MessageTypeFormat: Format[Value] = Json.formatEnum(MessageType)
}
