import  $name;format="space,Camel"$Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "$organization$"
ThisBuild / scalaVersion := "$scalaVersion$"

lazy val $name;format="space,camel"$ = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "$name$",
    libraryDependencies ++= rootDependencies
  )
