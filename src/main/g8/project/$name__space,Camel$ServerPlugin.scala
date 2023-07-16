import giter8.ScaffoldPlugin
import giter8.ScaffoldPlugin.autoImport.g8Scaffold
import sbt.{AutoPlugin, Def, _}
import complete.DefaultParsers._
import sbt.Keys._

import scala.sys.process.Process

object $name;format="space,Camel"$ServerPlugin extends AutoPlugin {

  object autoImport {
    val $name;format="space,camel"$GraphqlCodegenSleepDuration =
      settingKey[Long]("The maximum duration to wait for the development server to start up during UI GraphQL code generation.")
    val $name;format="space,camel"$UiDirectory    = settingKey[File]("The directory that holds the UI resources.")
    val $name;format="space,camel"$Controller     = inputKey[Unit]("Generate a controller class from the giter8 scaffold.")
    val $name;format="space,camel"$Model          = inputKey[Unit]("Generate a model class from the giter8 scaffold.")
    val $name;format="space,camel"$Module         = inputKey[Unit]("Generate a guice module from the giter8 scaffold.")
    val $name;format="space,camel"$Extension      = inputKey[Unit]("Generate an actor system extension from the giter8 scaffold.")
    val $name;format="space,camel"$GraphqlSchema  = inputKey[Unit]("Generate a GraphQL schema class from the giter8 scaffold.")
    val $name;format="space,camel"$GraphqlCodegen = inputKey[Unit]("Execute GraphQL codegen for the UI.")
    val $name;format="space,camel"$AngularUi      = inputKey[Unit]("Generate an Angular project.")
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
    val useSubPackage                      = optionalSubPackageName.isDefined
    val subPackageName                     = optionalSubPackageName.getOrElse("")

    if (useSubPackage) {
      g8Scaffold.toTask(
        s" model --base_name=\$baseName --use_sub_package=\$useSubPackage --sub_package_name=\$subPackageName"
      )
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
    val gqlCodegen    = Process("npm" :: "run" :: "gqlcodegen" :: Nil, $name;format="space,camel"$UiDirectory.value)
    val sbtRun        = Process("sbt" :: "$name;format="norm"$-server/run" :: Nil)
    val sleepDuration = $name;format="space,camel"$GraphqlCodegenSleepDuration.value

    Def.task {
      val playProcess = sbtRun.run
      Thread.sleep(sleepDuration)
      gqlCodegen.!
      playProcess.destroy()
    }
  }

  val ngNewTask = Def.taskDyn {
    val scriptPath                = (baseDirectory.value / "angular" / "new.sh").getCanonicalPath
    val installationDirectoryName = $name;format="space,camel"$UiDirectory.value.getName
    val ngNew                     = Process(scriptPath :: installationDirectoryName :: Nil, baseDirectory.value)
    Def.task(ngNew.!)
  }

  val $name;format="space,camel"$AngularUiTask = Def.taskDyn {
    Def.sequential((Compile/compile).toTask, ngNewTask, $name;format="space,camel"$GraphqlCodegenTask)
  }

  val baseProjectSettings: Seq[Def.Setting[_]] = Seq(
    $name;format="space,camel"$GraphqlCodegenSleepDuration := 30000,
    $name;format="space,camel"$UiDirectory := baseDirectory.value / "ui",
    $name;format="space,camel"$Controller := $name;format="space,camel"$ControllerTask.evaluated,
    $name;format="space,camel"$Model := $name;format="space,camel"$ModelTask.evaluated,
    $name;format="space,camel"$Module := $name;format="space,camel"$ModuleTask.evaluated,
    $name;format="space,camel"$Extension := $name;format="space,camel"$ExtensionTask.evaluated,
    $name;format="space,camel"$GraphqlSchema := $name;format="space,camel"$GraphqlSchemaTask.evaluated,
    $name;format="space,camel"$GraphqlCodegen := $name;format="space,camel"$GraphqlCodegenTask.value,
    $name;format="space,camel"$AngularUi := $name;format="space,camel"$AngularUiTask.value,
  )

  override val trigger = noTrigger

  override val requires: Plugins = ScaffoldPlugin

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseProjectSettings
}
