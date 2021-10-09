package config

case class GraphQLConfig(maxQueryDepth: Int, complexityThreshold: Double, rejectTypeNameIntrospection: Boolean)
