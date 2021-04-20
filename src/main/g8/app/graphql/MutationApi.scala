package graphql

import sangria.macros.derive.GraphQLField

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class MutationApi @Inject() (implicit graphQLContext: GraphQLContext, ec: ExecutionContext) {

  @GraphQLField
  def noOp(): Option[Boolean] = None
}
