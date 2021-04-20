package graphql

import models.auth.{CommonProfile, UserProfile}
import org.pac4j.core.profile.{CommonProfile => Pac4jCommonProfile}
import sangria.macros.derive.GraphQLField

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

class QueryApi @Inject() (implicit graphQLContext: GraphQLContext, ec: ExecutionContext) {

  @GraphQLField
  def userProfiles: Seq[UserProfile] = graphQLContext.request.profiles.collect {
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
