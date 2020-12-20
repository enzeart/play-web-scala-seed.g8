package graphql

import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest

trait GraphQLContextFactory {

  def create(request: AuthenticatedRequest[CommonProfile, _]): GraphQLContext
}
