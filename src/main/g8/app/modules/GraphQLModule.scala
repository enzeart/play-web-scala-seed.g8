package modules

import com.google.inject.{AbstractModule, Provides}
import config.AppConfig
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
  def provideQueryReducers(appConfig: AppConfig, environment: Environment): QueryReducers = {
    val rejectComplexQueries = QueryReducer.rejectComplexQueries[GraphQLContext](
      appConfig.graphql.complexityThreshold,
      (c, _) =>
        new IllegalArgumentException(
          s"Query complexity threshold exceeded: \$c/\${appConfig.graphql.complexityThreshold}"
        )
    )
    val rejectMaxDepth = QueryReducer.rejectMaxDepth[GraphQLContext](appConfig.graphql.maxQueryDepth)
    val rejectIntrospection =
      QueryReducer.rejectIntrospection[GraphQLContext](appConfig.graphql.rejectTypeNameIntrospection)
    val defaultQueryReducers    = rejectMaxDepth :: rejectComplexQueries :: Nil
    val productionQueryReducers = rejectIntrospection :: Nil

    new QueryReducers(
      reducers = if (environment.isProd) productionQueryReducers ::: defaultQueryReducers else defaultQueryReducers
    )
  }
}
