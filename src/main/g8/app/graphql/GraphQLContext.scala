package graphql

import com.google.inject.assistedinject.Assisted

import javax.inject.Inject

class GraphQLContext @Inject() (@Assisted val query: QueryApi, @Assisted val mutation: MutationApi)
