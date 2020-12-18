package graphql

import com.google.inject.assistedinject.Assisted
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest
import sangria.macros.derive.GraphQLField

import javax.inject.Inject

class MutationApi @Inject() (@Assisted request: AuthenticatedRequest[CommonProfile, _]) {

  @GraphQLField
  def noOp(): Option[Boolean] = None
}
