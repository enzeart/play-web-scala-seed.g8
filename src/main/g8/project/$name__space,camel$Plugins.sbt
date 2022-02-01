// Utilities
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "$scalafmt_version$")
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "$sbt_giter8_scaffold_version$")

addDependencyTreePlugin

// Play Framework
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "$play_framework_version$")

// Protobuf
addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc" % "$sbt_akka_grpc_version$")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "$sbt_protoc_version$")

libraryDependencies ++= Seq(
  "com.lightbend.play" %% "play-grpc-generators" % "$play_grpc_generators_version$",
  "com.thesamet.scalapb" %% "compilerplugin"           % "$scalapb_compilerplugin_version$",
  "com.thesamet.scalapb" %% "scalapb-validate-codegen" % "$scalapb_validate_codegen_version$"
)
