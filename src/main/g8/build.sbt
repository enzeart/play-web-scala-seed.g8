import $name;format="space,Camel"$Keys._
import play.grpc.gen.scaladsl.PlayScalaClientCodeGenerator
import scalapb.GeneratorOption._
import play.sbt.PlayImport.PlayKeys.devSettings
import play.sbt.PlayImport.PlayKeys._

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val `$name;format="norm"$` = (project in file("."))
  .aggregate(
    `$name;format="norm"$-server`,
    `$name;format="norm"$-core`
  )
  .settings(
    name := "$name;format="norm"$",
    inThisBuild(
      Seq(
        version := "0.0.0",
        organization := "$organization$",
        scalaVersion := "$scala_version$",
        $if(codeartifact_support_enabled.truthy) $
        codeArtifactUrl := "$codeartifact_url$"
        $endif$
      )
    )
  )

lazy val `$name;format="norm"$-core` = (project in file("$name;format="norm"$-core"))
  .enablePlugins(AkkaGrpcPlugin)
  .settings(
    name := "$name;format="norm"$-core",
    libraryDependencies ++= $name;format="space,Camel"$Dependencies.coreDependencies,
    dependencyOverrides ++= $name;format="space,Camel"$Dependencies.coreDependencyOverrides,
    libraryDependencies ++= $name;format="space,Camel"$Dependencies.protobufDependencies,
    libraryDependencies ++= $name;format="space,Camel"$Dependencies.protobufServiceDependencies,
    dependencyOverrides ++= $name;format="space,Camel"$Dependencies.protobufDependencyOverrides,
    akkaGrpcExtraGenerators ++= Seq(PlayScalaClientCodeGenerator),
    akkaGrpcGeneratedSources := Seq(AkkaGrpc.Client),
    g8ScaffoldTemplatesDirectory := baseDirectory.value / ".." / ".g8",
    Compile / PB.targets += scalapb.validate
      .gen(FlatPackage) -> (Compile / akkaGrpcCodeGeneratorSettings / target).value
  )

lazy val `$name;format="norm"$-server` = (project in file("$name;format="norm"$-server"))
  .enablePlugins(PlayScala, $name;format="space,Camel"$ServerPlugin)
  .dependsOn(`$name;format="norm"$-core`)
  .settings(
    name := "$name;format="norm"$-server",
    libraryDependencies ++= $name;format="space,Camel"$Dependencies.serverDependencies,
    dependencyOverrides ++= $name;format="space,Camel"$Dependencies.serverDependencyOverrides,
    devSettings ++= Seq(
    ),
    Universal / packageName := name.value,
    topLevelDirectory := Option(packageName.value),
    g8ScaffoldTemplatesDirectory := baseDirectory.value / ".." / ".g8",
    playRunHooks += {
      UserInterfaceHook((Compile / appUiDirectory).value)
    }
  )
