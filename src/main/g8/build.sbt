import Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "$organization$"
ThisBuild / scalaVersion := "$scalaVersion$"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "$name$",
    libraryDependencies ++= rootDependencies
  )
