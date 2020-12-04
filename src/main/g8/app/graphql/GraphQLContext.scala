package graphql

import javax.inject.Inject
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest

class GraphQLContext @Inject() (request: AuthenticatedRequest[CommonProfile, _]) {}
