package controllers

import config.AppConfig
import graphql.{GraphQLConstants, GraphQLContextFactory, GraphQLSchema}
import javax.inject.{Inject, Singleton}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.Environment
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, Result}
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}
import sangria.renderer.SchemaRenderer
import utils.StringConstants

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class GraphQLController @Inject() (
    val controllerComponents: SecurityComponents,
    graphQLContextFactory: GraphQLContextFactory,
    appConfig: AppConfig,
    environment: Environment
)(
    implicit ec: ExecutionContext
) extends BaseController
    with Security[CommonProfile] {

  def graphql(query: String, variables: Option[String], operation: Option[String]): Action[AnyContent] =
    Secure(appConfig.auth.clientName).async { request =>
      executeQuery(request, query, variables map parseVariables, operation)
    }

  def graphqlBody: Action[JsValue] = Secure(appConfig.auth.clientName).async(parse.json) { request =>
    val query     = (request.body \ GraphQLConstants.QueryFieldQuery).as[String]
    val operation = (request.body \ GraphQLConstants.QueryFieldOperation).asOpt[String]

    val variables = (request.body \ GraphQLConstants.QueryFieldVariables).toOption.flatMap {
      case JsString(vars) => Option(parseVariables(vars))
      case obj: JsObject  => Option(obj)
      case _              => None
    }

    executeQuery(request, query, variables, operation)
  }

  def graphiql: Action[AnyContent] = Secure {
    if (environment.isProd) {
      NotFound
    } else {
      Ok(views.html.graphiql())
    }
  }

  private def parseVariables(variables: String): JsObject = {
    if (variables.trim == StringConstants.Empty || variables.trim == StringConstants.JsonNull) Json.obj()
    else Json.parse(variables).as[JsObject]
  }

  private def executeQuery(
      request: AuthenticatedRequest[_],
      query: String,
      variables: Option[JsObject],
      operation: Option[String]
  ): Future[Result] = {
    QueryParser.parse(query) match {
      case Success(queryAst) =>
        Executor
          .execute(
            schema = GraphQLSchema.Root,
            queryAst = queryAst,
            userContext = graphQLContextFactory.create(request),
            operationName = operation,
            variables = variables getOrElse Json.obj()
          )
          .map(Ok(_))
          .recover {
            case error: QueryAnalysisError => BadRequest(error.resolveError)
            case error: ErrorWithResolver  => InternalServerError(error.resolveError)
          }
      case Failure(error: SyntaxError) =>
        Future.successful(
          BadRequest(
            Json.obj(
              GraphQLConstants.SyntaxError -> error.getMessage(),
              GraphQLConstants.Location -> Json.arr(
                Json.obj(GraphQLConstants.Line -> error.originalError.position.line),
                GraphQLConstants.Column -> error.originalError.position.column
              )
            )
          )
        )
      case Failure(exception) =>
        Future.successful(InternalServerError(exception.getMessage))
    }
  }
}
