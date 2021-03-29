package modules

import com.google.inject.{AbstractModule, Provides}
import config.AppConfig
import graphql.GraphQLContext
import modules.GraphQLModule.QueryReducers
import net.codingwell.scalaguice.ScalaModule
import sangria.execution.QueryReducer

import javax.inject.Singleton

object GraphQLModule {

  class QueryReducers(val reducers: List[QueryReducer[GraphQLContext, _]])
}

class GraphQLModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = ()

  @Provides @Singleton
  def provideQueryReducers(appConfig: AppConfig): QueryReducers = {
    val rejectComplexQueries = QueryReducer.rejectComplexQueries[GraphQLContext](
      appConfig.graphql.complexityThreshold,
      (c, _) =>
        new IllegalArgumentException(
          s"Query complexity threshold exceeded: \$c/\${appConfig.graphql.complexityThreshold}"
        )
    )
    val rejectMaxDepth = QueryReducer.rejectMaxDepth[GraphQLContext](appConfig.graphql.maxQueryDepth)

    new QueryReducers(rejectMaxDepth :: rejectComplexQueries :: Nil)
  }
}
