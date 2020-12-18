package graphql

trait GraphQLContextFactory {

  def create(query: QueryApi, mutation: MutationApi): GraphQLContext
}
