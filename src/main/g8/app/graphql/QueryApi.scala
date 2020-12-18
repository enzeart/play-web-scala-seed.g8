package graphql

import com.google.inject.assistedinject.Assisted
import models.auth.{CommonProfile, UserProfile}
import org.pac4j.play.scala.AuthenticatedRequest
import org.pac4j.core.profile.{CommonProfile => Pac4jCommonProfile}
import sangria.macros.derive.GraphQLField

import scala.jdk.CollectionConverters._
import javax.inject.Inject

class QueryApi @Inject() (
    @Assisted request: AuthenticatedRequest[Pac4jCommonProfile, _]
) {

  @GraphQLField
  def userProfiles: Seq[UserProfile] = request.profiles.collect {
    case profile: Pac4jCommonProfile =>
      CommonProfile(
        id = profile.getId,
        userName = Option(profile.getUsername),
        email = Option(profile.getEmail),
        firstName = Option(profile.getFirstName),
        familyName = Option(profile.getFirstName),
        displayName = Option(profile.getDisplayName),
        roles = profile.getRoles.asScala.toSet
      )
  }
}
