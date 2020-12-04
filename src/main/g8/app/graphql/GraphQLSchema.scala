package graphql

import sangria.schema._

object GraphQLSchema {

  val Query: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Query",
    fields[GraphQLContext, Unit](
      AuthSchema.UserProfilesField
    )
  )

  val Root: Schema[GraphQLContext, Unit] = Schema(Query, additionalTypes = AuthSchema.Types)
}
