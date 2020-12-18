package graphql

import sangria.schema._
import sangria.execution.deferred.DeferredResolver
import sangria.macros.derive._
import AuthSchema._

object GraphQLSchema {

  val Query: ObjectType[GraphQLContext, Unit] = deriveContextObjectType[GraphQLContext, QueryApi, Unit](_.query)

  val Mutation: ObjectType[GraphQLContext, Unit] =
    deriveContextObjectType[GraphQLContext, MutationApi, Unit](_.mutation)

  val Subscription: ObjectType[GraphQLContext, Unit] = ObjectType(
    GraphQLConstants.SchemaSubscription,
    fields[GraphQLContext, Unit]()
  )

  val Root: Schema[GraphQLContext, Unit] =
    Schema(query = Query, mutation = Option(Mutation), subscription = None, additionalTypes = AuthSchema.Types)

  val Resolver: DeferredResolver[GraphQLContext] = DeferredResolver.fetchers()
}
