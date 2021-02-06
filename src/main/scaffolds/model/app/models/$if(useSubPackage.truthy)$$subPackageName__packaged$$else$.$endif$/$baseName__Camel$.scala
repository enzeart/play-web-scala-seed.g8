package models$if(useSubPackage.truthy)$.$subPackageName;format="package"$$endif$

import play.api.libs.json._

object $baseName;format="Camel"$ {

  implicit val $baseName;format="camel"$Format: Format[$baseName;format="Camel"$] = Json.format[$baseName;format="Camel"$]
}

case class $baseName;format="Camel"$(_deleteThisField: String)
