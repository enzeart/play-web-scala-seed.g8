import giter8.ScaffoldPlugin
import giter8.ScaffoldPlugin.autoImport.g8Scaffold
import sbt.{AutoPlugin, Def, _}
import complete.DefaultParsers._
import sbt.Keys._

import scala.sys.process.Process

object $name;format="space,Camel"$ServerPlugin extends AutoPlugin {

  object autoImport {
    val $name;format="space,camel"$GraphqlCodegenSleepDuration = settingKey[Long]("The duration that the graphql code generation task will wait for the dev server to start up")
    val $name;format="space,camel"$UiDirectory = settingKey[File]("The directory that holds the UI resources")
    val $name;format="space,camel"$Controller = inputKey[Unit]("Create a controller from the giter8 scaffold")
    val $name;format="space,camel"$Model = inputKey[Unit]("Create a model from the giter8 scaffold")
    val $name;format="space,camel"$Module = inputKey[Unit]("Create a guice module from the giter8 scaffold")
    val $name;format="space,camel"$Extension = inputKey[Unit]("Create an actor system extension from the giter8 scaffold")
    val $name;format="space,camel"$GraphqlSchema = inputKey[Unit]("Create a graphQL schema from the giter8 scaffold")
    val $name;format="space,camel"$GraphqlCodegen = inputKey[Unit]("Run the graphql codegen")
    val $name;format="space,camel"$AngularUi = inputKey[Unit]("Create an angular project")
    val $name;format="space,camel"$AppStart = inputKey[Unit]("Start the application")
  }

  import autoImport._

  val baseNameParser = Space ~> token(StringBasic).examples("<base_name>")

  val optionalDirectoryNameParser = (Space ~> token(StringBasic).examples("<directoryName>")).?

  val optionalSubPackageNameParser = (Space ~> token(StringBasic).examples("<subPackageName")).?

  val $name;format="space,camel"$ControllerTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" controller --base_name=\${baseNameParser.parsed}")
  }

  val $name;format="space,camel"$ModelTask = Def.inputTaskDyn {
    val (baseName, optionalSubPackageName) = (baseNameParser ~ optionalSubPackageNameParser).parsed
    val useSubPackage = optionalSubPackageName.isDefined
    val subPackageName = optionalSubPackageName.getOrElse("")

    if (useSubPackage) {
      g8Scaffold.toTask(s" model --base_name=\$baseName --use_sub_package=\$useSubPackage --sub_package_name=\$subPackageName")
    } else {
      g8Scaffold.toTask(s" model --base_name=\$baseName")
    }
  }

  val $name;format="space,camel"$ModuleTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" module --base_name=\${baseNameParser.parsed}")
  }

  val $name;format="space,camel"$ExtensionTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" extension --base_name=\${baseNameParser.parsed}")
  }

  val $name;format="space,camel"$GraphqlSchemaTask = Def.inputTaskDyn {
    g8Scaffold.toTask(s" graphqlSchema --base_name=\${baseNameParser.parsed}")
  }

  val $name;format="space,camel"$GraphqlCodegenTask = Def.taskDyn {
    val gqlCodegen = Process("npm" :: "run" :: "gqlcodegen" :: Nil, $name;format="space,camel"$UiDirectory.value)
    val sbtRun = Process("sbt" :: "$name;format="norm"$-server/run" :: Nil)
    val sleepDuration = $name;format="space,camel"$GraphqlCodegenSleepDuration.value

    Def.task {
      val playProcess = sbtRun.run
      Thread.sleep(sleepDuration)
      gqlCodegen.!
      playProcess.destroy()
    }
  }

  val ngNewTask = Def.taskDyn {
    val scriptPath = (baseDirectory.value / "angular" / "new.sh").getCanonicalPath
    val installationDirectoryName = $name;format="space,camel"$UiDirectory.value.getName
    val ngNew = Process(scriptPath :: installationDirectoryName :: Nil, baseDirectory.value)
    Def.task(ngNew.!)
  }

  val $name;format="space,camel"$AngularUiTask = Def.taskDyn {
    Def.sequential(ngNewTask, $name;format="space,camel"$GraphqlCodegenTask)
  }

  val $name;format="space,camel"$AppStartTask = Def.taskDyn {
    val npmStart = Process("npm" :: "run" :: "start" :: Nil, $name;format="space,camel"$UiDirectory.value)
    val sbtRun = Process("sbt" :: "$name;format="norm"$-server/run" :: Nil)

    Def.task {
      val playProcess = sbtRun.run
      npmStart.!
      playProcess.destroy()
    }
  }

  val base$name;format="space,Camel"$ProjectSettings: Seq[Def.Setting[_]] = Seq(
    $name;format="space,camel"$GraphqlCodegenSleepDuration := 30000,
    $name;format="space,camel"$UiDirectory := baseDirectory.value / "ui",
    $name;format="space,camel"$Controller := $name;format="space,camel"$ControllerTask.evaluated,
    $name;format="space,camel"$Model := $name;format="space,camel"$ModelTask.evaluated,
    $name;format="space,camel"$Module := $name;format="space,camel"$ModuleTask.evaluated,
    $name;format="space,camel"$Extension := $name;format="space,camel"$ExtensionTask.evaluated,
    $name;format="space,camel"$GraphqlSchema := $name;format="space,camel"$GraphqlSchemaTask.evaluated,
    $name;format="space,camel"$GraphqlCodegen := $name;format="space,camel"$GraphqlCodegenTask.value,
    $name;format="space,camel"$AngularUi := $name;format="space,camel"$AngularUiTask.value,
    $name;format="space,camel"$AppStart := $name;format="space,camel"$AppStartTask.value
  )

  override val trigger = noTrigger

  override val requires: Plugins = ScaffoldPlugin

  override lazy val projectSettings: Seq[Def.Setting[_]] = base$name;format="space,Camel"$ProjectSettings
}
