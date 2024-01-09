package modules

import com.google.inject.multibindings.ProvidesIntoSet
import com.google.inject.{AbstractModule, Provides}
import config.{AppServerConfig, AuthConfig}
import net.codingwell.scalaguice.ScalaModule
import org.pac4j.core.client.direct.AnonymousClient
import org.pac4j.core.client.{Client, Clients}
import org.pac4j.core.config.Config
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.profile.CommonProfile
$if(oidc_enabled.truthy)$
import org.pac4j.oidc.client.OidcClient
import org.pac4j.oidc.config.OidcConfiguration
$endif$
import org.pac4j.play.scala.{DefaultSecurityComponents, Pac4jScalaTemplateHelper, SecurityComponents}
import org.pac4j.play.store.PlayCacheSessionStore
import org.pac4j.play.{CallbackController, LogoutController}
import play.Environment

import javax.inject.{Inject, Singleton}
import scala.jdk.CollectionConverters.IterableHasAsScala

class AuthModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[SessionStore].to[PlayCacheSessionStore]

    bind[SecurityComponents].to[DefaultSecurityComponents]

    bind[Pac4jScalaTemplateHelper[CommonProfile]]

    val callbackController = new CallbackController()
    bind[CallbackController].toInstance(callbackController)

    val logoutController = new LogoutController()
    bind[LogoutController].toInstance(logoutController)
  }

  @Singleton
  @Provides
  def provideAuthConfig(appServerConfig: AppServerConfig): AuthConfig = appServerConfig.auth

  @Inject
  def configureCallbackController(callbackController: CallbackController, authConfig: AuthConfig): Unit = {
    callbackController.setDefaultUrl(authConfig.defaultLoginUrl)
  }

  @Inject
  def configureLogoutController(logoutController: LogoutController, authConfig: AuthConfig): Unit = {
    logoutController.setDefaultUrl(authConfig.defaultLogoutUrl)
  }

  $if(oidc_enabled.truthy)$
  @Singleton
  @ProvidesIntoSet
  def provideOidcClient(authConfig: AuthConfig): Client = {
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
  def provideAnonymousClient(authConfig: AuthConfig): Client = {
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

  private def anonymizeClients(clients: Seq[Client]): Seq[Client] =
    clients.map(c => anonymizeClient(c.getName))

  @Singleton
  @Provides
  def provideConfig(
      environment: Environment,
      authConfig: AuthConfig,
      clientSet: java.util.Set[Client],
      sessionStore: SessionStore
  ): Config = {
    val clients   = new Clients(authConfig.callbackUrl)
    val config    = new Config(clients)
    val clientSeq = clientSet.asScala.toSeq

    if (environment.isProd) {
      clients.setClients(clientSeq: _*)
    } else {
      clients.setClients(anonymizeClients(clientSeq): _*)
    }

    config.setSessionStoreFactory(_ => sessionStore)

    config
  }
}
