package graphql

import sangria.macros.derive.GraphQLField

import javax.inject.Inject

class MutationApi @Inject() (implicit graphQLContext: GraphQLContext) {

  @GraphQLField
  def noOp(): Option[Boolean] = None
}
