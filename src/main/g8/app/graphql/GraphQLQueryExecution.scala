package graphql

import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest
import play.api.libs.json.{JsObject, Json}
import sangria.ast.Document
import sangria.execution.{ExecutionScheme, Executor}
import sangria.marshalling.{InputUnmarshaller, ResultMarshaller}

import scala.concurrent.ExecutionContext

trait GraphQLQueryExecution {

  def executeStandardQuery(
      request: AuthenticatedRequest[CommonProfile, _],
      queryAst: Document,
      variables: Option[JsObject],
      operation: Option[String]
  )(
      implicit executionContext: ExecutionContext,
      marshaller: ResultMarshaller,
      um: InputUnmarshaller[JsObject],
      scheme: ExecutionScheme,
      graphQLContextFactory: GraphQLContextFactory
  ): scheme.Result[GraphQLContext, marshaller.Node] =
    Executor
      .execute(
        schema = GraphQLSchema.Root,
        queryAst = queryAst,
        userContext = graphQLContextFactory.create(request),
        operationName = operation,
        variables = variables getOrElse Json.obj(),
        deferredResolver = GraphQLSchema.Resolver
      )
}
