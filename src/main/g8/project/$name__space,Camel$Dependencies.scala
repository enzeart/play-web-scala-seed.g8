import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._
import sbtprotoc.ProtocPlugin.ProtobufSrcConfig

object $name;format="space,Camel"$Dependencies {

  // Utilities
  val pureconfig: ModuleID = "com.github.pureconfig" %% "pureconfig" % "$pureconfig_version$"

  val scalaGuice: ModuleID = "net.codingwell" %% "scala-guice" % "$scala_guice_version$"

  // Play Framework
  val scalatestplusPlay: ModuleID = "org.scalatestplus.play" %% "scalatestplus-play" % "$scalatestplus_play_version$" % Test

  $if(oidc_enabled.truthy)$
  val jacksonDatabind: ModuleID = "com.fasterxml.jackson.core" % "jackson-databind" % "$jackson_databind_version$"
  $endif$

  // Auth
  val playPac4j: ModuleID = "org.pac4j" %% "play-pac4j" % "$play_pac4j_version$"

  $if(oidc_enabled.truthy)$
  val pac4jOidc: ModuleID = "org.pac4j" % "pac4j-oidc" % "$pac4j_oidc_version$"
  $endif$

  // GraphQL
  val sangria: ModuleID = "org.sangria-graphql" %% "sangria" % "$sangria_version$"

  val sangriaPlayJson: ModuleID = "org.sangria-graphql" %% "sangria-play-json" % "$sangria_play_json_version$"

  // Protobuf
  val playGrpcRuntime: ModuleID = "com.lightbend.play" %% "play-grpc-runtime" % "$play_grpc_runtime_version$"

  val scalapbLenses: ModuleID = "com.thesamet.scalapb" %% "lenses" % "$scalapb_lenses_version$"

  val scalapbRuntime: ModuleID = "com.thesamet.scalapb" %% "scalapb-runtime" % "$scalapb_runtime_version$"

  // Akka
  val akkaActorTestkitTyped: ModuleID =
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % PlayVersion.akkaVersion % Test

  val akkaDiscovery: ModuleID = "com.typesafe.akka" %% "akka-discovery" % PlayVersion.akkaVersion

  val akkaHttp: ModuleID = "com.typesafe.akka" %% "akka-http" % PlayVersion.akkaHttpVersion

  val akkaHttp2Support: ModuleID = "com.typesafe.akka" %% "akka-http2-support" % PlayVersion.akkaHttpVersion

  val akkaHttpSprayJson: ModuleID = "com.typesafe.akka" %% "akka-http-spray-json" % PlayVersion.akkaHttpVersion

  val akkaStreamTyped: ModuleID = "com.typesafe.akka" %% "akka-stream-typed" % PlayVersion.akkaVersion

  val akkaStreamTestkit: ModuleID = "com.typesafe.akka" %% "akka-stream-testkit" % PlayVersion.akkaVersion % Test

  val protobufDependencies: Seq[ModuleID] = Seq(
    playGrpcRuntime
  )

  val protobufDependencyOverrides: Seq[ModuleID] = Seq(
    scalapbLenses,
    scalapbRuntime,
  )

  val protobufServiceDependencies: Seq[ModuleID] = Seq[ModuleID](

  ).map(_  % ProtobufSrcConfig intransitive())

  val coreDependencies: Seq[ModuleID] = Seq(

  )

  val coreDependencyOverrides: Seq[ModuleID] = Seq(

  )

  val serverDependencies: Seq[ModuleID] = Seq(
    akkaActorTestkitTyped,
    akkaStreamTestkit,
    akkaStreamTyped,
    caffeine,
    guice,
    playPac4j,
    pureconfig,
    sangria,
    sangriaPlayJson,
    scalaGuice,
    scalatestplusPlay,
    $if(oidc_enabled.truthy)$
    pac4jOidc,
    $endif$
  )

  val serverDependencyOverrides: Seq[ModuleID] = Seq(
    akkaDiscovery,
    akkaHttp,
    akkaHttp2Support,
    akkaHttpSprayJson,
    $if(oidc_enabled.truthy)$
    jacksonDatabind,
    $endif$
  )
}
