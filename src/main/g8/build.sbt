import $name;format="space,Camel"$Dependencies._
import play.sbt.PlayImport.PlayKeys.devSettings

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val `$name;format="norm"$` = (project in file("."))
  .enablePlugins(PlayScala, $name;format="space,Camel"$Plugin)
  .settings(
    name := "$name;format="norm"$",
    libraryDependencies ++= rootDependencies,
    devSettings ++= Seq(
      "play.server.https.port" -> "9443"
    ),
    topLevelDirectory := Option(packageName.value),
    inThisBuild(Seq(
      organization := "$organization$",
      scalaVersion := "$scalaVersion$"
    ))
  )
