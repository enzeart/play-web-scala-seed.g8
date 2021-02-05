import giter8.ScaffoldPlugin
import giter8.ScaffoldPlugin.autoImport.g8Scaffold
import sbt.{AutoPlugin, Def, _}
import complete.DefaultParsers._
import sbt.Keys._
import sbt.internal.util.complete.Parser

import scala.sys.process.Process

object $name;format="space,Camel"$Plugin extends AutoPlugin {

  object autoImport {
    lazy val $name;format="space,camel"$Controller = inputKey[Unit]("Create a controller from the giter8 scaffold")
    lazy val $name;format="space,camel"$Model = inputKey[Unit]("Create a model from the giter8 scaffold")
    lazy val $name;format="space,camel"$Module = inputKey[Unit]("Create a guice module from the giter8 scaffold")
    lazy val $name;format="space,camel"$GraphqlSchema = inputKey[Unit]("Create a graphQL schema from the giter8 scaffold")
    lazy val $name;format="space,camel"$AngularUi = inputKey[Unit]("Create an angular project")
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

  val $name;format="space,camel"$AngularUiTaskParser = (Space ~> token(StringBasic).examples("<directoryName>")).?

  val $name;format="space,camel"$AngularUiTask = Def.inputTaskDyn {
    val installationDirectory = $name;format="space,camel"$AngularUiTaskParser.parsed.getOrElse("ui")
    val ngNew = Process("./angular/new.sh" :: installationDirectory :: Nil, baseDirectory.value)
    val gqlCodegen = Process("npm run gqlcodegen", baseDirectory.value / installationDirectory)
    val sbtRun = Process("sbt run", baseDirectory.value)

    Def.task {
      val playProcess = sbtRun.run
      (ngNew #&& gqlCodegen).!
      playProcess.destroy()
    }
  }

  val base$name;format="space,Camel"$Settings: Seq[Def.Setting[_]] = Seq(
    $name;format="space,camel"$Controller := $name;format="space,camel"$ControllerTask.evaluated,
    $name;format="space,camel"$Model := $name;format="space,camel"$ModelTask.evaluated,
    $name;format="space,camel"$Module := $name;format="space,camel"$ModuleTask.evaluated,
    $name;format="space,camel"$GraphqlSchema := $name;format="space,camel"$GraphqlSchemaTask.evaluated,
    $name;format="space,camel"$AngularUi := $name;format="space,camel"$AngularUiTask.evaluated
  )

  override val trigger = noTrigger

  override val requires: Plugins = ScaffoldPlugin

  override lazy val projectSettings: Seq[Def.Setting[_]] = base$name;format="space,Camel"$Settings
}
