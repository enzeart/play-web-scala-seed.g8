package modules

import com.google.inject.{AbstractModule, Provides}
import config.AppConfig
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration

import javax.inject.Singleton

class ConfigModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = ()

  @Singleton
  @Provides
  def provideAppConfig(configuration: Configuration): AppConfig = configuration.get[AppConfig](AppConfig.Paths.Root)
}
