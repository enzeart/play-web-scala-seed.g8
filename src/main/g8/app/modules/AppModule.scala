package modules

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import graphql.{GraphQLContextFactory, MutationApiFactory, QueryApiFactory}
import net.codingwell.scalaguice.ScalaModule

class AppModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    install((new FactoryModuleBuilder).build(classOf[GraphQLContextFactory]))
    install((new FactoryModuleBuilder).build(classOf[QueryApiFactory]))
    install((new FactoryModuleBuilder).build(classOf[MutationApiFactory]))
  }
}
