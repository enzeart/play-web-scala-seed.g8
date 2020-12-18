package graphql

import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest

trait QueryApiFactory {

  def create(request: AuthenticatedRequest[CommonProfile, _]): QueryApi
}
