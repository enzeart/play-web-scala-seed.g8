package graphql.apollo

import graphql.apollo.MessageType._
import play.api.libs.json._

object OperationMessage {

  private val TypeNamingMap: Map[String, String] = Map(
    classOf[ConnectionInit].getCanonicalName      -> GqlConnectionInit.toString,
    classOf[ConnectionError].getCanonicalName     -> GqlConnectionError.toString,
    classOf[ConnectionAck].getCanonicalName       -> GqlConnectionAck.toString,
    classOf[ConnectionKeepAlive].getCanonicalName -> GqlConnectionKeepAlive.toString
  )

  implicit val JsonConf: JsonConfiguration = JsonConfiguration(
    discriminator = "type",
    typeNaming = JsonNaming { fqcn =>
      TypeNamingMap.getOrElse(fqcn, throw new IllegalStateException(s"Missing type naming mapping for class \$fqcn"))
    }
  )

  implicit val ConnectionInitFormat: OFormat[ConnectionInit] = Json.format[ConnectionInit]

  implicit val ConnectionErrorFormat: OFormat[ConnectionError] = Json.format[ConnectionError]

  implicit val ConnectionAckFormat: OFormat[ConnectionAck] = Json.format[ConnectionAck]

  implicit val ConnectionKeepAliveFormation: OFormat[ConnectionKeepAlive] = Json.format[ConnectionKeepAlive]

  implicit val OperationMessageFormat: OFormat[OperationMessage] = Json.format[OperationMessage]
}

sealed trait OperationMessage {
  val `type`: MessageType.Value
}

case class ConnectionInit(payload: JsObject, `type`: MessageType.Value = GqlConnectionInit) extends OperationMessage {
  require(`type` == GqlConnectionInit)
}

case class ConnectionError(payload: JsObject, `type`: MessageType.Value = GqlConnectionError) extends OperationMessage {
  require(`type` == GqlConnectionError)
}

case class ConnectionAck(payload: JsObject = Json.obj(), `type`: MessageType.Value = GqlConnectionAck)
    extends OperationMessage {
  require(`type` == GqlConnectionAck)
}

object ConnectionKeepAlive {
  val Instance: ConnectionKeepAlive = ConnectionKeepAlive()
}

case class ConnectionKeepAlive(`type`: MessageType.Value = GqlConnectionKeepAlive) extends OperationMessage {
  require(`type` == GqlConnectionKeepAlive)
}
