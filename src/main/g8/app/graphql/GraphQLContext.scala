package graphql

import com.google.inject.assistedinject.Assisted
import javax.inject.Inject
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest

class GraphQLContext @Inject() (
    @Assisted request: AuthenticatedRequest[CommonProfile, _]
) {}
