package controllers

import config.AppConfig
import javax.inject.{Inject, Singleton}
import graphql.{GraphQLContext, SchemaDefinition}
import org.pac4j.core.profile.CommonProfile
import play.api.mvc.{Action, AnyContent, BaseController, Result}
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.parser.{QueryParser, SyntaxError}
import sangria.marshalling.playJson._
import sangria.renderer.SchemaRenderer
import graphql.GraphQLConstants
import utils.StringConstants

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class GraphQLController @Inject() (
    val controllerComponents: SecurityComponents,
    graphQLContext: GraphQLContext,
    appConfig: AppConfig
)(
    implicit ec: ExecutionContext
) extends BaseController
    with Security[CommonProfile] {

  def graphql(query: String, variables: Option[String], operation: Option[String]): Action[AnyContent] =
    Secure(appConfig.auth.clientName) {
      Action.async { _ => executeQuery(query, variables map parseVariables, operation) }
    }

  def graphqlBody: Action[JsValue] = Secure(appConfig.auth.clientName) {
    Action.async(parse.json) { request =>
      val query     = (request.body \ GraphQLConstants.Query).as[String]
      val operation = (request.body \ GraphQLConstants.Operation).asOpt[String]

      val variables = (request.body \ GraphQLConstants.Variables).toOption.flatMap {
        case JsString(vars) => Option(parseVariables(vars))
        case obj: JsObject  => Option(obj)
        case _              => None
      }

      executeQuery(query, variables, operation)
    }
  }

  def renderSchema: Action[AnyContent] = Secure(appConfig.auth.clientName) {
    Action {
      Ok(SchemaRenderer.renderSchema(SchemaDefinition.Root))
    }
  }

  private def parseVariables(variables: String): JsObject = {
    if (variables.trim == StringConstants.Empty || variables.trim == StringConstants.JsonNull) Json.obj()
    else Json.parse(variables).as[JsObject]
  }

  private def executeQuery(query: String, variables: Option[JsObject], operation: Option[String]): Future[Result] = {
    QueryParser.parse(query) match {
      case Success(queryAst) =>
        Executor
          .execute(
            schema = SchemaDefinition.Root,
            queryAst = queryAst,
            userContext = graphQLContext,
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
              GraphQLConstants.Locations -> Json.arr(
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