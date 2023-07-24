import giter8.ScaffoldPlugin
import giter8.ScaffoldPlugin.autoImport.g8Scaffold
import sbt.{AutoPlugin, Def, _}
import complete.DefaultParsers._
import sbt.Keys._

import scala.sys.process.Process

object $name;format="space,Camel"$ServerPlugin extends AutoPlugin {

  object autoImport {
    val appUiDirectory = settingKey[File]("The directory containing UI assets.")
    val appControllerScaffold = inputKey[Unit]("Generate a controller class from the giter8 scaffold.")
    val appModuleScaffold = inputKey[Unit]("Generate a guice module from the giter8 scaffold.")
    val appExtensionScaffold = inputKey[Unit]("Generate an actor system extension from the giter8 scaffold.")
    val appGraphqlSchemaScaffold = inputKey[Unit]("Generate a GraphQL schema class from the giter8 scaffold.")
    val appAngularInit = inputKey[Unit]("Initialize an Angular project.")
  }

  import autoImport._

  val baseNameParser = Space ~> token(StringBasic).examples("<base_name>")

  val appControllerScaffoldTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" controller --base_name=\${baseNameParser.parsed}")
  }

  val appModuleScaffoldTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" module --base_name=\${baseNameParser.parsed}")
  }

  val appExtensionScaffoldTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" extension --base_name=\${baseNameParser.parsed}")
  }

  val appGraphqlSchemaScaffoldTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" graphqlSchema --base_name=\${baseNameParser.parsed}")
  }

  val appAngularInitTask = Def.taskDyn {
    val scriptPath                = (baseDirectory.value / "angular" / "init.sh").getCanonicalPath
    val installationDirectoryName = appUiDirectory.value.getName
    val ngNew                     = Process(scriptPath :: installationDirectoryName :: Nil, baseDirectory.value)
    Def.task(ngNew.!)
  }

  val baseProjectSettings: Seq[Def.Setting[_]] = Seq(
    appUiDirectory := baseDirectory.value / "ui",
    appControllerScaffold := appControllerScaffoldTask.evaluated,
    appModuleScaffold := appModuleScaffoldTask.evaluated,
    appExtensionScaffold := appExtensionScaffoldTask.evaluated,
    appGraphqlSchemaScaffold := appGraphqlSchemaScaffoldTask.evaluated,
    appAngularInit := appAngularInitTask.value,
  )

  override val trigger = noTrigger

  override val requires: Plugins = ScaffoldPlugin

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseProjectSettings
}
