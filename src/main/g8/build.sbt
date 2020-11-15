import  $name;format="space,Camel"$Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val $name;format="space,camel"$ = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "$name$",
    libraryDependencies ++= rootDependencies,
    inThisBuild(Seq(
      organization := "$organization$",
      scalaVersion := "$scalaVersion$"
    ))
  )
