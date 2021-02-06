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
    lazy val $name;format="space,camel"$GraphqlCodegen = inputKey[Unit]("Run the graphql codgen")
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

  val optionalDirectoryNameParser = (Space ~> token(StringBasic).examples("<directoryName>")).?

  val $name;format="space,camel"$GraphqlCodegenTask = Def.inputTaskDyn {
    val uiDirectory = optionalDirectoryNameParser.parsed.getOrElse("ui")
    val gqlCodegen = Process("npm run gqlcodegen", baseDirectory.value / uiDirectory)
    val sbtRun = Process("sbt $name;format="norm"$/run")

    Def.task {
      val playProcess = sbtRun.run
      Thread.sleep(15000)
      gqlCodegen.!
      playProcess.destroy()
    }
  }

  val ngNewTask = Def.inputTaskDyn {
    val installationDirectory = optionalDirectoryNameParser.parsed.getOrElse("ui")
    val ngNew = Process("./angular/new.sh" :: installationDirectory :: Nil, baseDirectory.value)
    Def.task(ngNew.!)
  }

  val playTestAngularUiTask = Def.inputTaskDyn {
    val installationDirectory = optionalDirectoryNameParser.parsed.getOrElse("ui")
    val in = s" \$installationDirectory"

    Def.sequential(ngNewTask.toTask(in), $name;format="space,camel"$GraphqlCodegenTask.toTask(in))
  }

  val base$name;format="space,Camel"$Settings: Seq[Def.Setting[_]] = Seq(
    $name;format="space,camel"$Controller := $name;format="space,camel"$ControllerTask.evaluated,
    $name;format="space,camel"$Model := $name;format="space,camel"$ModelTask.evaluated,
    $name;format="space,camel"$Module := $name;format="space,camel"$ModuleTask.evaluated,
    $name;format="space,camel"$GraphqlSchema := $name;format="space,camel"$GraphqlSchemaTask.evaluated,
    $name;format="space,camel"$GraphqlCodegen := $name;format="space,camel"$GraphqlCodegenTask.evaluated,
    $name;format="space,camel"$AngularUi := $name;format="space,camel"$AngularUiTask.evaluated
  )

  override val trigger = noTrigger

  override val requires: Plugins = ScaffoldPlugin

  override lazy val projectSettings: Seq[Def.Setting[_]] = base$name;format="space,Camel"$Settings
}
