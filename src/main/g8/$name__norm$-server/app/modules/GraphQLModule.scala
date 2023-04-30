package modules

import com.google.inject.{AbstractModule, Provides}
import config.AppServerConfig
import graphql.GraphQLContext
import modules.GraphQLModule.QueryReducers
import net.codingwell.scalaguice.ScalaModule
import play.Environment
import sangria.execution.QueryReducer

import javax.inject.Singleton

object GraphQLModule {

  class QueryReducers(val reducers: List[QueryReducer[GraphQLContext, _]])
}

class GraphQLModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = ()

  @Provides @Singleton
  def provideQueryReducers(appServerConfig: AppServerConfig, environment: Environment): QueryReducers = {
    val rejectComplexQueries = QueryReducer.rejectComplexQueries[GraphQLContext](
      appServerConfig.graphql.complexityThreshold,
      (c, _) =>
        new IllegalArgumentException(
          s"Query complexity threshold exceeded: \$c/\${appServerConfig.graphql.complexityThreshold}"
        )
    )
    val rejectMaxDepth = QueryReducer.rejectMaxDepth[GraphQLContext](appServerConfig.graphql.maxQueryDepth)
    val rejectIntrospection =
      QueryReducer.rejectIntrospection[GraphQLContext](appServerConfig.graphql.rejectTypeNameIntrospection)
    val defaultQueryReducers    = rejectMaxDepth :: rejectComplexQueries :: Nil
    val productionQueryReducers = rejectIntrospection :: Nil

    new QueryReducers(
      reducers = if (environment.isProd) productionQueryReducers ::: defaultQueryReducers else defaultQueryReducers
    )
  }
}
