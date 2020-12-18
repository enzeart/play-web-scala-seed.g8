import giter8.ScaffoldPlugin
import giter8.ScaffoldPlugin.autoImport.g8Scaffold
import sbt.{AutoPlugin, Def, _}
import complete.DefaultParsers._
import sbt.internal.util.complete.Parser

object $name;format="space,Camel"$Plugin extends AutoPlugin {

  object autoImport {
    lazy val $name;format="space,camel"$Controller = inputKey[Unit]("Create a controller from the giter8 scaffold")
    lazy val $name;format="space,camel"$Model = inputKey[Unit]("Create a model from the giter8 scaffold")
    lazy val $name;format="space,camel"$Module = inputKey[Unit]("Create a guice module from the giter8 scaffold")
    lazy val $name;format="space,camel"$GraphqlSchema = inputKey[Unit]("Create a graphQL schema from the giter8 scaffold")
  }

  import autoImport._

  val baseNameParser: Parser[String] = Space ~> token(ScalaID).examples("<baseName>")

  val $name;format="space,camel"$ControllerTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" controller --baseName=\${baseNameParser.parsed}")
  }

  val $name;format="space,camel"$ModelTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" model --baseName=\${baseNameParser.parsed}")
  }

  val $name;format="space,camel"$ModuleTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" module --baseName=\${baseNameParser.parsed}")
  }

  val $name;format="space,camel"$GraphqlSchemaTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" graphqlSchema --baseName=\${baseNameParser.parsed}")
  }

  val base$name;format="space,Camel"$Settings: Seq[Def.Setting[_]] = Seq(
    $name;format="space,camel"$Controller := $name;format="space,camel"$ControllerTask.evaluated,
    $name;format="space,camel"$Model := $name;format="space,camel"$ModelTask.evaluated,
    $name;format="space,camel"$Module := $name;format="space,camel"$ModuleTask.evaluated,
    $name;format="space,camel"$GraphqlSchema := $name;format="space,camel"$GraphqlSchemaTask.evaluated
  )

  override val trigger = noTrigger

  override val requires: Plugins = ScaffoldPlugin

  override lazy val projectSettings: Seq[Def.Setting[_]] = base$name;format="space,Camel"$Settings
}