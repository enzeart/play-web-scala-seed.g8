import giter8.ScaffoldPlugin
import giter8.ScaffoldPlugin.autoImport.g8Scaffold
import sbt.{AutoPlugin, Def, _}
import complete.DefaultParsers._
import sbt.Keys._

import scala.sys.process.Process

object $name;format="space,Camel"$ServerPlugin extends AutoPlugin {

  object autoImport {
    val appUiDirectory    = settingKey[File]("The directory that holds the UI resources.")
    val appController     = inputKey[Unit]("Generate a controller class from the giter8 scaffold.")
    val appModule         = inputKey[Unit]("Generate a guice module from the giter8 scaffold.")
    val appExtension      = inputKey[Unit]("Generate an actor system extension from the giter8 scaffold.")
    val appGraphqlSchema  = inputKey[Unit]("Generate a GraphQL schema class from the giter8 scaffold.")
    val appAngularInit  = inputKey[Unit]("Initialize an Angular project.")
  }

  import autoImport._

  val baseNameParser = Space ~> token(StringBasic).examples("<base_name>")

  val optionalDirectoryNameParser = (Space ~> token(StringBasic).examples("<directoryName>")).?

  val optionalSubPackageNameParser = (Space ~> token(StringBasic).examples("<subPackageName>")).?

  val appControllerTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" controller --base_name=\${baseNameParser.parsed}")
  }

  val appModuleTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" module --base_name=\${baseNameParser.parsed}")
  }

  val appExtensionTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" extension --base_name=\${baseNameParser.parsed}")
  }

  val appGraphqlSchemaTask = Def.inputTaskDyn {
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
    appController := appControllerTask.evaluated,
    appModule := appModuleTask.evaluated,
    appExtension := appExtensionTask.evaluated,
    appGraphqlSchema := appGraphqlSchemaTask.evaluated,
    appAngularInit := appAngularInitTask.value,
  )

  override val trigger = noTrigger

  override val requires: Plugins = ScaffoldPlugin

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseProjectSettings
}
