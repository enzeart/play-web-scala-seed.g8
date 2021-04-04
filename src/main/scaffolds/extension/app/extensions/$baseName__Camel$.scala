package extensions

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}

object $baseName;format="Camel"$ extends ExtensionId[$baseName;format="Camel"$] {

  override def createExtension(system: ActorSystem[_]): $baseName;format="Camel"$ = new $baseName;format="Camel"$(system)
}

class $baseName;format="Camel"$(system: ActorSystem[_]) extends Extension {}
