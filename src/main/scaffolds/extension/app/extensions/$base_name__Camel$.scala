package extensions

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}

object $base_name;format="Camel"$ extends ExtensionId[$base_name;format="Camel"$] {

  override def createExtension(system: ActorSystem[_]): $base_name;format="Camel"$ = new $base_name;format="Camel"$(system)
}

class $base_name;format="Camel"$(system: ActorSystem[_]) extends Extension {}
