import $name;format="space,Camel"$Keys._
import giter8.ScaffoldPlugin
import giter8.ScaffoldPlugin.autoImport.g8Scaffold
import sbt.{AutoPlugin, Def, _}
import complete.DefaultParsers._
import sbt.Keys._

import scala.sys.process.Process

object $name;format="space,Camel"$ServerPlugin extends AutoPlugin {

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
