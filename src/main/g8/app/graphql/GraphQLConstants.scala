package graphql

object GraphQLConstants {

  object QueryFieldName {
    val Query: String     = "query"
    val Operation: String = "operation"
    val Variables: String = "variables"
  }

  val SyntaxError: String = "syntaxError"
  val Location: String    = "location"
  val Line: String        = "line"
  val Column: String      = "column"
}
