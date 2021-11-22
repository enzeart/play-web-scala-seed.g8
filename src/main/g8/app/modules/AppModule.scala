package modules

import com.google.inject.assistedinject.FactoryModuleBuilder
import graphql.GraphQLContextFactory

import com.google.inject.{AbstractModule, Provides}
import config.AppConfig
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import javax.inject.Singleton

class AppModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    install((new FactoryModuleBuilder).build(classOf[GraphQLContextFactory]))
  }

  @Provides @Singleton
  def provideAppConfig(configuration: Configuration): AppConfig =
    ConfigSource.fromConfig(configuration.underlying).at("app").loadOrThrow[AppConfig]
}
