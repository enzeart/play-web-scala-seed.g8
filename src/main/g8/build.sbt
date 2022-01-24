import play.sbt.PlayImport.PlayKeys.devSettings

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val `$name;format="norm"$` = (project in file("."))
  .aggregate(
    `$name;format="norm"$-server`
  )
  .settings(
    name := "$name;format="norm"$",
    inThisBuild(Seq(
      organization := "$organization$",
      scalaVersion := "$scala_version$"
    ))
  )

lazy val `$name;format="norm"$-server` = (project in file("$name;format="norm"$-server"))
  .enablePlugins(PlayScala, $name;format="space,Camel"$Plugin)
  .settings(
    name := "$name;format="norm"$-server",
    libraryDependencies ++= $name;format="space,Camel"$Dependencies.serverDependencies,
    devSettings ++= Seq(
      "play.server.https.port" -> "9443"
    ),
    topLevelDirectory := Option(packageName.value),
    g8ScaffoldTemplatesDirectory := baseDirectory.value / ".." / ".g8"
  )
