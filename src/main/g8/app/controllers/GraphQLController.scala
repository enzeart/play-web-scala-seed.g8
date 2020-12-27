package controllers

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import config.AppConfig
import graphql.SubscriptionsTransportWsConnection.{Disconnect, PayloadData, Protocol}
import graphql.{GraphQLConstants, GraphQLContextFactory, _}
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.{AuthenticatedRequest, SecureAction, Security, SecurityComponents}
import play.Environment
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{Action, AnyContent, AnyContentAsEmpty, BaseController, Request, Result, WebSocket}
import sangria.ast.OperationType.{Mutation, Query, Subscription}
import sangria.execution.{ErrorWithResolver, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}
import utils.{StreamUtil, StringConstants}

import scala.jdk.CollectionConverters._
import scala.jdk.FutureConverters._
import org.pac4j.play.java.{SecureAction => SecureJavaAction}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import akka.actor.typed.scaladsl.adapter._
import akka.stream.Materializer
import play.api.mvc.WebSocket.MessageFlowTransformer.jsonMessageFlowTransformer

@Singleton
class GraphQLController @Inject() (
    val controllerComponents: SecurityComponents,
    val graphQLContextFactory: GraphQLContextFactory,
    appConfig: AppConfig,
    environment: Environment
)(
    implicit ec: ExecutionContext,
    actorSystem: ActorSystem,
    mat: Materializer
) extends BaseController
    with Security[CommonProfile]
    with GraphQLQueryExecution {

  implicit class SecureWebsocket(secureAction: SecureAction[CommonProfile, AnyContent, AuthenticatedRequest]) {

    def webSocket[In, Out](
        f: AuthenticatedRequest[AnyContent] => Future[Either[Result, Flow[In, Out, _]]]
    )(implicit transformer: MessageFlowTransformer[In, Out]): WebSocket =
      WebSocket.acceptOrResult[In, Out] { request =>
        val webContext       = new PlayWebContext(request, playSessionStore)
        val secureJavaAction = new SecureJavaAction(config, playSessionStore)
        secureJavaAction
          .call(
            webContext,
            secureAction.clients,
            secureAction.authorizers,
            secureAction.matchers,
            secureAction.multiProfile
          )
          .asScala
          .flatMap(r =>
            if (r == null) { // TODO: Clean up null check
              val profileManager = new ProfileManager[CommonProfile](webContext)
              val profiles       = profileManager.getAllLikeDefaultSecurityLogic(true)
              f(
                AuthenticatedRequest(
                  profiles.asScala.toList,
                  webContext.supplementRequest(request.asJava).asScala.withBody(AnyContentAsEmpty)
                )
              )
            } else {
              Future successful Left(r.asScala)
            }
          )
      }
  }

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
        .actorFlow[JsValue, Protocol, JsValue](
          inputTransform = PayloadData,
          inputRef = outputRef =>
            Future.successful(
              actorSystem.spawn(SubscriptionsTransportWsConnection(outputRef, request, graphQLContextFactory), "name") // TODO: Pick a more suitable name
            ),
          inputOnCompleteMessage = Disconnect,
          inputOnFailureMessage = _ => Disconnect
        )
        .map(Right(_))
    }(jsonMessageFlowTransformer)

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
