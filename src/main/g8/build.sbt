import play.core.PlayVersion
import play.grpc.gen.scaladsl.PlayScalaClientCodeGenerator
import scalapb.GeneratorOption._
import play.sbt.PlayImport.PlayKeys.devSettings

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val `$name;format="norm"$` = (project in file("."))
  .aggregate(
    `$name;format="norm"$-server`,
    `$name;format="norm"$-protobuf`
  )
  .settings(
    name := "$name;format="norm"$",
    inThisBuild(Seq(
      organization := "$organization$",
      scalaVersion := "$scala_version$"
    ))
  )

lazy val `$name;format="norm"$-protobuf` = (project in file("$name;format="norm"$-protobuf"))
  .enablePlugins(AkkaGrpcPlugin)
  .settings(
    name := "$name;format="norm"$-protobuf",
    libraryDependencies ++= $name;format="space,Camel"$Dependencies.protobufDependencies,
    dependencyOverrides ++= $name;format="space,Camel"$Dependencies.protobufDependencyOverrides,
    akkaGrpcExtraGenerators ++= Seq(PlayScalaClientCodeGenerator),
    akkaGrpcGeneratedSources := Seq(AkkaGrpc.Client),
    Compile / PB.targets += scalapb.validate.gen(FlatPackage) -> (Compile / akkaGrpcCodeGeneratorSettings / target).value
  )


lazy val `$name;format="norm"$-server` = (project in file("$name;format="norm"$-server"))
  .enablePlugins(PlayScala, $name;format="space,Camel"$Plugin)
  .dependsOn(`$name;format="norm"$-protobuf`)
  .settings(
    name := "$name;format="norm"$-server",
    libraryDependencies ++= $name;format="space,Camel"$Dependencies.serverDependencies,
    dependencyOverrides ++= $name;format="space,Camel"$Dependencies.serverDependencyOverrides,
    devSettings ++= Seq(
    ),
    topLevelDirectory := Option(packageName.value),
    g8ScaffoldTemplatesDirectory := baseDirectory.value / ".." / ".g8"
  )
