package graphql.apollo

import play.api.libs.json._

object MessageType extends Enumeration {

  val GqlConnectionInit: Value = Value("connection_init")

  val GqlStart: Value = Value("start")

  val GqlStop: Value = Value("stop")

  val GqlConnectionTerminate: Value = Value("connection_terminate")

  val GqlConnectionError: Value = Value("connection_error")

  val GqlConnectionAck: Value = Value("connection_ack")

  val GqlData: Value = Value("data")

  val GqlError: Value = Value("error")

  val GqlComplete: Value = Value("complete")

  val GqlConnectionKeepAlive: Value = Value("ka")

  implicit val MessageTypeFormat: Format[Value] = Json.formatEnum(MessageType)
}
