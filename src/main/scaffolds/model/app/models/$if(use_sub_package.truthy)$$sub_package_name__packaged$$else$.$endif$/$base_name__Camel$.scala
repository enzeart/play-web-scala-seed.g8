package models$if(use_sub_package.truthy)$.$sub_package_name;format="package"$$endif$

import play.api.libs.json._

object $base_name;format="Camel"$ {

  implicit val $base_name;format="camel"$Format: Format[$base_name;format="Camel"$] = Json.format[$base_name;format="Camel"$]
}

case class $base_name;format="Camel"$(_deleteThisField: String)
