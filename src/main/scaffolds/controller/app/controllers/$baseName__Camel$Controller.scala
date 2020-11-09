package controllers

import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import javax.inject.{Inject, Singleton}
import play.api.mvc.BaseController

@Singleton
class $baseName;format="Camel"$Controller @Inject() (val controllerComponents: SecurityComponents)
    extends BaseController
    with Security[CommonProfile] {}