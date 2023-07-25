import sbt.{File, settingKey, inputKey}

object $name;format="space,Camel"$Keys {
  val appUiDirectory = settingKey[File]("The directory containing UI assets.")
  val appControllerScaffold = inputKey[Unit]("Generate a controller class from the giter8 scaffold.")
  val appModuleScaffold = inputKey[Unit]("Generate a guice module from the giter8 scaffold.")
  val appExtensionScaffold = inputKey[Unit]("Generate an actor system extension from the giter8 scaffold.")
  val appGraphqlSchemaScaffold = inputKey[Unit]("Generate a GraphQL schema class from the giter8 scaffold.")
  val appAngularInit = inputKey[Unit]("Initialize an Angular project.")
}
