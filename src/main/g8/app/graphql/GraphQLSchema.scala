package graphql

import sangria.schema._
import sangria.execution.deferred.DeferredResolver

object GraphQLSchema {

  val Query: ObjectType[GraphQLContext, Unit] = ObjectType(
    GraphQLConstants.SchemaQuery,
    fields[GraphQLContext, Unit](
      AuthSchema.UserProfilesField
    )
  )

  val Mutation: ObjectType[GraphQLContext, Unit] = ObjectType(
    GraphQLConstants.SchemaMutation,
    fields[GraphQLContext, Unit]()
  )

  val Subscription: ObjectType[GraphQLContext, Unit] = ObjectType(
    GraphQLConstants.SchemaSubscription,
    fields[GraphQLContext, Unit]()
  )

  val Root: Schema[GraphQLContext, Unit] =
    Schema(query = Query, mutation = None, subscription = None, additionalTypes = AuthSchema.Types)

  val Resolver: DeferredResolver[GraphQLContext] = DeferredResolver.fetchers()
}
