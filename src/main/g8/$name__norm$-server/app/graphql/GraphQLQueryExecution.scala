package graphql

import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.AuthenticatedRequest
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import sangria.ast.Document
import sangria.execution.{ExecutionScheme, Executor, QueryReducer}
import sangria.marshalling.{InputUnmarshaller, ResultMarshaller}
import utils.StringConstants

import scala.concurrent.ExecutionContext

trait GraphQLQueryExecution {

  protected def extractQueryFields(json: JsValue): (String, Option[String], Option[JsObject]) = {
    val query     = (json \ GraphQLConstants.QueryFieldName.Query).as[String]
    val operation = (json \ GraphQLConstants.QueryFieldName.Operation).asOpt[String]

    val variables = (json \ GraphQLConstants.QueryFieldName.Variables).toOption.flatMap {
      case JsString(vars) => Option(parseVariables(vars))
      case obj: JsObject  => Option(obj)
      case _              => None
    }

    (query, operation, variables)
  }

  protected def parseVariables(variables: String): JsObject = {
    if (variables.trim == StringConstants.Empty || variables.trim == StringConstants.JsonNull) Json.obj()
    else Json.parse(variables).as[JsObject]
  }

  def executeStandardQuery(
      request: AuthenticatedRequest[CommonProfile, _],
      queryAst: Document,
      variables: Option[JsObject],
      operation: Option[String],
      queryReducers: List[QueryReducer[GraphQLContext, _]]
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
        deferredResolver = GraphQLSchema.Resolver,
        queryReducers = queryReducers
      )
}
