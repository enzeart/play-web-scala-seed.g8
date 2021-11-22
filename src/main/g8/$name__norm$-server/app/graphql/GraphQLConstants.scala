package graphql

object GraphQLConstants {

  object QueryFieldName {
    val Query: String     = "query"
    val Operation: String = "operation"
    val Variables: String = "variables"
  }

  object ErrorFieldName {
    val Message: String   = "message"
    val Locations: String = "locations"
    val Line: String      = "line"
    val Column: String    = "column"
  }
}
