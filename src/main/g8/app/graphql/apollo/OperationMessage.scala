package graphql.apollo

import play.api.libs.json._

object OperationMessage {

  val GqlConnectionInit: String = "connection_init"
  val GqlUnknown: String        = "unknown"

  private val TypeNamingMap: Map[String, String] = Map(
    classOf[ConnectionInit].getCanonicalName -> GqlConnectionInit
  )

  implicit val JsonConf: JsonConfiguration = JsonConfiguration(
    discriminator = "type",
    typeNaming = JsonNaming { fqcn =>
      TypeNamingMap.getOrElse(fqcn, throw new IllegalStateException(s"Missing type naming mapping for class \$fqcn"))
    }
  )

  implicit val ConnectionInitFormat: OFormat[ConnectionInit] = Json.format[ConnectionInit]

  implicit val OperationMessageFormat: OFormat[OperationMessage] = Json.format[OperationMessage]
}

sealed trait OperationMessage

case class ConnectionInit(payload: JsObject) extends OperationMessage
