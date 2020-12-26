import $name;format="space,Camel"$Dependencies._
import play.sbt.PlayImport.PlayKeys.devSettings

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val $name;format="space,camel"$ = (project in file("."))
  .enablePlugins(PlayScala, $name;format="space,Camel"$Plugin)
  .settings(
    name := "$name$",
    libraryDependencies ++= rootDependencies,
    devSettings ++= Seq(
      "play.server.https.port" -> "9443"
    ),
    inThisBuild(Seq(
      organization := "$organization$",
      scalaVersion := "$scalaVersion$"
    ))
  )
