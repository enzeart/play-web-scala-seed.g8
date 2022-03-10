package modules

import com.google.inject.multibindings.ProvidesIntoSet
import com.google.inject.{AbstractModule, Provides}
import config.{AppConfig, AuthConfig}
import net.codingwell.scalaguice.ScalaModule
import org.pac4j.core.client.direct.AnonymousClient
import org.pac4j.core.client.{Client, Clients}
import org.pac4j.core.config.Config
import org.pac4j.core.credentials.Credentials
$if(oidc_enabled.truthy)$
import org.pac4j.oidc.client.OidcClient
import org.pac4j.oidc.config.OidcConfiguration
$endif$
import org.pac4j.play.scala.{DefaultSecurityComponents, SecurityComponents}
import org.pac4j.play.store.{PlayCacheSessionStore, PlaySessionStore}
import org.pac4j.play.{CallbackController, LogoutController}
import play.Environment
import play.libs.concurrent.HttpExecutionContext

import javax.inject.Singleton
import scala.jdk.CollectionConverters.IterableHasAsScala

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

  $if(oidc_enabled.truthy)$
  @Singleton
  @ProvidesIntoSet
  def provideOidcClient(authConfig: AuthConfig): Client[_ <: Credentials] = {
    val configuration = new OidcConfiguration
    configuration.setClientId(authConfig.clientId)
    configuration.setSecret(authConfig.secret)
    configuration.setDiscoveryURI(authConfig.discoveryUri)
    val client = new OidcClient(configuration)
    client.setName(authConfig.clientName)
    client
  }
  $else$

  @Singleton
  @ProvidesIntoSet
  def provideAnonymousClient(authConfig: AuthConfig): Client[_ <: Credentials] = {
    val client = AnonymousClient.INSTANCE
    client.setName(authConfig.clientName)
    client
  }
  $endif$

  private def anonymizeClient(clientName: String): AnonymousClient = {
    val client = new AnonymousClient
    client.setName(clientName)
    client
  }

  private def anonymizeClients(clients: Seq[Client[_ <: Credentials]]): Seq[Client[_ <: Credentials]] =
    clients.map(c => anonymizeClient(c.getName))

  @Singleton
  @Provides
  def provideConfig(
      environment: Environment,
      authConfig: AuthConfig,
      clientSet: java.util.Set[Client[_ <: Credentials]]
  ): Config = {
    val clients   = new Clients(authConfig.callbackUrl)
    val config    = new Config(clients)
    val clientSeq = clientSet.asScala.toSeq

    if (environment.isProd) {
      clients.setClients(clientSeq: _*)
    } else {
      clients.setClients(anonymizeClients(clientSeq): _*)
    }

    config
  }
}
