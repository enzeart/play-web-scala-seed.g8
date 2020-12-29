package controllers

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import akka.stream.Materializer
import config.AppConfig
import graphql.apollo.SubscriptionsTransportWsConnection.{Disconnect, PayloadData, Protocol}
import graphql.apollo.{SubscriptionsTransportWsConnection, randomSubscriptionsTransportWsConnectionName}
import graphql.{GraphQLConstants, GraphQLContextFactory, _}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.Environment
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.mvc._
import sangria.ast.OperationType.{Mutation, Query, Subscription}
import sangria.execution.{ErrorWithResolver, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}
import utils.{StreamUtil, StringConstants}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class GraphQLController @Inject() (
    val controllerComponents: SecurityComponents,
    appConfig: AppConfig,
    environment: Environment
)(
    implicit ec: ExecutionContext,
    actorSystem: ActorSystem,
    mat: Materializer,
    graphQLContextFactory: GraphQLContextFactory
) extends BaseController
    with Security[CommonProfile]
    with WebSocketSecurity[CommonProfile]
    with GraphQLQueryExecution {

  def graphql(query: String, variables: Option[String], operation: Option[String]): Action[AnyContent] =
    Secure(appConfig.auth.clientName).async { request =>
      executeQuery(request, query, variables map parseVariables, operation)
    }

  def graphqlBody: Action[JsValue] = Secure(appConfig.auth.clientName).async(parse.json) { request =>
    val query     = (request.body \ GraphQLConstants.QueryFieldName.Query).as[String]
    val operation = (request.body \ GraphQLConstants.QueryFieldName.Operation).asOpt[String]

    val variables = (request.body \ GraphQLConstants.QueryFieldName.Variables).toOption.flatMap {
      case JsString(vars) => Option(parseVariables(vars))
      case obj: JsObject  => Option(obj)
      case _              => None
    }

    executeQuery(request, query, variables, operation)
  }

  def subscriptionsTransportWsWebSocket: WebSocket =
    Secure(appConfig.auth.clientName).webSocket { request =>
      StreamUtil
        .actorFlow[String, Protocol, String](
          inputTransform = PayloadData,
          inputRef = outputRef =>
            Future.successful(
              actorSystem
                .spawn(
                  SubscriptionsTransportWsConnection(
                    outputRef,
                    request,
                    appConfig.graphql.subscriptionsTransportWsKeepAliveInterval
                  ),
                  randomSubscriptionsTransportWsConnectionName
                )
            ),
          inputOnCompleteMessage = Disconnect,
          inputOnFailureMessage = _ => Disconnect
        )
        .map(Right(_))
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
        queryAst.operationType(operation) match {
          case Some(Query) | Some(Mutation) =>
            executeStandardQuery(request, queryAst, variables, operation)
              .map(Ok(_))
              .recover {
                case error: QueryAnalysisError => BadRequest(error.resolveError)
                case error: ErrorWithResolver  => InternalServerError(error.resolveError)
              }
          case Some(Subscription) | None =>
            Future.successful(
              BadRequest(
                Json.obj(
                  GraphQLConstants.ErrorFieldName.Message -> ErrorMessages.unsupportedOperationType(Subscription)
                )
              )
            )
        }
      case Failure(error: SyntaxError) =>
        Future.successful(
          BadRequest(
            Json.obj(
              GraphQLConstants.ErrorFieldName.Message -> error.getMessage(),
              GraphQLConstants.ErrorFieldName.Locations -> Json.arr(
                Json.obj(
                  GraphQLConstants.ErrorFieldName.Line   -> error.originalError.position.line,
                  GraphQLConstants.ErrorFieldName.Column -> error.originalError.position.column
                )
              )
            )
          )
        )
      case Failure(exception) =>
        Future.successful(InternalServerError(exception.getMessage))
    }
  }
}
