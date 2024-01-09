// Utilities
addSbtPlugin("org.scalameta"            % "sbt-scalafmt"        % "$sbt_scalafmt_version$")
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "$sbt_giter8_scaffold_version$")

addDependencyTreePlugin

// Play Framework
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "$play_framework_version$")

// Protobuf
addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc" % "$sbt_akka_grpc_version$")
addSbtPlugin("com.thesamet"            % "sbt-protoc"    % "$sbt_protoc_version$")

libraryDependencies ++= Seq(
  "com.lightbend.play"   %% "play-grpc-generators"     % "$play_grpc_generators_version$",
  "com.thesamet.scalapb" %% "compilerplugin"           % "$scalapb_compilerplugin_version$"
)

// Git
libraryDependencies ++= Seq(
  "org.eclipse.jgit" % "org.eclipse.jgit" % "6.8.0.202311291450-r"
)

$if(codeartifact_support_enabled.truthy)$
// AWS CodeArtifact Support
addSbtPlugin("io.github.bbstilson" % "sbt-codeartifact" % "$sbt_codeartifact_version$")
$endif$

// Dependency Conflicts
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
