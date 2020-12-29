package graphql.apollo

import graphql.apollo.MessageType._
import play.api.libs.json._

object OperationMessage {

  private val TypeNamingMap: Map[String, String] = Map(
    classOf[ConnectionInit].getCanonicalName      -> GqlConnectionInit.toString,
    classOf[Start].getCanonicalName               -> GqlStart.toString,
    classOf[Stop].getCanonicalName                -> GqlStop.toString,
    classOf[ConnectionTerminate].getCanonicalName -> GqlConnectionTerminate.toString,
    classOf[ConnectionError].getCanonicalName     -> GqlConnectionError.toString,
    classOf[ConnectionAck].getCanonicalName       -> GqlConnectionAck.toString,
    classOf[Data].getCanonicalName                -> GqlData.toString,
    classOf[Error].getCanonicalName               -> GqlError.toString,
    classOf[Complete].getCanonicalName            -> GqlComplete.toString,
    classOf[ConnectionKeepAlive].getCanonicalName -> GqlConnectionKeepAlive.toString
  )

  implicit val JsonConf: JsonConfiguration = JsonConfiguration(
    discriminator = "type",
    typeNaming = JsonNaming { fqcn =>
      TypeNamingMap.getOrElse(fqcn, throw new IllegalStateException(s"Missing type naming mapping for class \$fqcn"))
    }
  )

  implicit val ConnectionInitFormat: OFormat[ConnectionInit] = Json.format[ConnectionInit]

  implicit val StartFormat: OFormat[Start] = Json.format[Start]

  implicit val StopFormat: OFormat[Stop] = Json.format[Stop]

  implicit val ConnectionTerminateFormat: OFormat[ConnectionTerminate] = Json.format[ConnectionTerminate]

  implicit val ConnectionErrorFormat: OFormat[ConnectionError] = Json.format[ConnectionError]

  implicit val ConnectionAckFormat: OFormat[ConnectionAck] = Json.format[ConnectionAck]

  implicit val DataFormat: OFormat[Data] = Json.format[Data]

  implicit val ErrorFormat: OFormat[Error] = Json.format[Error]

  implicit val CompleteFormat: OFormat[Complete] = Json.format[Complete]

  implicit val ConnectionKeepAliveFormation: OFormat[ConnectionKeepAlive] = Json.format[ConnectionKeepAlive]

  implicit val OperationMessageFormat: OFormat[OperationMessage] = Json.format[OperationMessage]
}

sealed trait OperationMessage {
  val `type`: MessageType.Value
}

case class ConnectionInit(payload: JsObject, `type`: MessageType.Value = GqlConnectionInit) extends OperationMessage {
  require(`type` == GqlConnectionInit)
}

case class Start(id: String, payload: JsObject, `type`: MessageType.Value = GqlStart) extends OperationMessage {
  require(`type` == GqlStart)
}

case class Stop(id: String, `type`: MessageType.Value = GqlStop) extends OperationMessage {
  require(`type` == GqlStop)
}

case class ConnectionTerminate(`type`: MessageType.Value = GqlConnectionTerminate) extends OperationMessage {
  require(`type` == GqlConnectionTerminate)
}

case class ConnectionError(payload: JsObject, `type`: MessageType.Value = GqlConnectionError) extends OperationMessage {
  require(`type` == GqlConnectionError)
}

case class ConnectionAck(payload: JsObject = Json.obj(), `type`: MessageType.Value = GqlConnectionAck)
    extends OperationMessage {
  require(`type` == GqlConnectionAck)
}

case class Data(id: String, payload: JsObject, `type`: MessageType.Value = GqlData) extends OperationMessage {
  require(`type` == GqlData)
}

case class Error(id: String, payload: JsObject, `type`: MessageType.Value = GqlError) extends OperationMessage {
  require(`type` == GqlError)
}

case class Complete(id: String, `type`: MessageType.Value = GqlComplete) extends OperationMessage {
  require(`type` == GqlComplete)
}

object ConnectionKeepAlive {
  val Instance: ConnectionKeepAlive = ConnectionKeepAlive()
}

case class ConnectionKeepAlive(`type`: MessageType.Value = GqlConnectionKeepAlive) extends OperationMessage {
  require(`type` == GqlConnectionKeepAlive)
}
