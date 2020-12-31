package graphql

import com.google.inject.assistedinject.Assisted
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest

import javax.inject.Inject

class GraphQLContext @Inject() (@Assisted val request: AuthenticatedRequest[CommonProfile, _]) {
  implicit private val graphQLContext: GraphQLContext = this
  val query: QueryApi                                 = new QueryApi
  val mutation: MutationApi                           = new MutationApi
}
