package graphql

import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest

trait MutationApiFactory {

  def create(request: AuthenticatedRequest[CommonProfile, _]): MutationApi
}
