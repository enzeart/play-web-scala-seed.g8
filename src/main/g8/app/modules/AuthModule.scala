package modules

import com.google.inject.{AbstractModule, Provides}
import config.{AppConfig, AuthConfig}
import net.codingwell.scalaguice.ScalaModule
import org.pac4j.core.client.Clients
import org.pac4j.core.client.direct.AnonymousClient
import org.pac4j.core.config.Config
import org.pac4j.play.scala.{DefaultSecurityComponents, SecurityComponents}
import org.pac4j.play.store.{PlayCacheSessionStore, PlaySessionStore}
import org.pac4j.play.{CallbackController, LogoutController}
import play.Environment
import play.libs.concurrent.HttpExecutionContext

import javax.inject.Singleton

class AuthModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[PlaySessionStore].to[PlayCacheSessionStore]
    bind[SecurityComponents].to[DefaultSecurityComponents]
  }

  @Singleton
  @Provides
  def provideAuthConfig(appConfig: AppConfig): AuthConfig = appConfig.auth

  @Singleton
  @Provides
  def provideCallbackController(
      executionContext: HttpExecutionContext,
      sessionStore: PlaySessionStore,
      config: Config,
      authConfig: AuthConfig
  ): CallbackController = {
    val callbackController = new CallbackController {
      ec = executionContext
      playSessionStore = sessionStore
    }

    callbackController.setDefaultUrl(authConfig.defaultLoginUrl)
    callbackController.setConfig(config)

    callbackController
  }

  @Singleton
  @Provides
  def provideLogoutController(
      executionContext: HttpExecutionContext,
      sessionStore: PlaySessionStore,
      config: Config,
      authConfig: AuthConfig
  ): LogoutController = {
    val logoutController = new LogoutController {
      ec = executionContext
      playSessionStore = sessionStore
    }

    logoutController.setDefaultUrl(authConfig.defaultLogoutUrl)
    logoutController.setConfig(config)

    logoutController
  }

  @Singleton
  @Provides
  def provideAnonymousClient(authConfig: AuthConfig): AnonymousClient = {
    val client = AnonymousClient.INSTANCE
    client.setName(authConfig.clientName)
    client
  }

  @Singleton
  @Provides
  def provideConfig(environment: Environment, authConfig: AuthConfig, anonymousClient: AnonymousClient): Config = {
    val clients = new Clients(authConfig.callbackUrl)
    val config  = new Config(clients)

    if (environment.isProd) {
      clients.setClients(anonymousClient)
    } else if (environment.isTest) {
      clients.setClients(anonymousClient)
    } else {
      clients.setClients(anonymousClient)
    }

    config
  }
}
