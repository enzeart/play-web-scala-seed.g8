package graphql

import AuthSchema.{Types => AuthSchemaTypes, _}
import sangria.execution.deferred.DeferredResolver
import sangria.macros.derive._
import sangria.schema._

object GraphQLSchema {

  val Query: ObjectType[GraphQLContext, Unit] = deriveContextObjectType[GraphQLContext, QueryApi, Unit](_.query)

  val Mutation: ObjectType[GraphQLContext, Unit] =
    deriveContextObjectType[GraphQLContext, MutationApi, Unit](_.mutation)

  val Root: Schema[GraphQLContext, Unit] =
    Schema(
      query = Query,
      mutation = Option(Mutation),
      additionalTypes = AuthSchemaTypes
    )

  val Resolver: DeferredResolver[GraphQLContext] = DeferredResolver.fetchers()
}
