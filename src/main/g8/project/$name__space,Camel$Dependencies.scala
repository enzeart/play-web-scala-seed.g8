import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object $name;format="space,Camel"$Dependencies {

  val playPac4j: ModuleID = "org.pac4j" %% "play-pac4j" % "$play_pac4j_version$"

  val scalaGuice: ModuleID = "net.codingwell" %% "scala-guice" % "$scala_guice_version$"

  val sangria: ModuleID = "org.sangria-graphql" %% "sangria" % "$sangria_version$"

  val sangriaPlayJson: ModuleID = "org.sangria-graphql" %% "sangria-play-json" % "$sangria_play_json_version$"

  val akkaStreamTyped: ModuleID = "com.typesafe.akka" %% "akka-stream-typed" % PlayVersion.akkaVersion

  val pureconfig: ModuleID = "com.github.pureconfig" %% "pureconfig" % "$pureconfig_version$"

  val scalatestplusPlay: ModuleID = "org.scalatestplus.play" %% "scalatestplus-play" % "$scalatestplus_play_version$" % Test

  val akkaStreamTestkit: ModuleID = "com.typesafe.akka" %% "akka-stream-testkit" % PlayVersion.akkaVersion % Test

  val akkaActorTestkitTyped: ModuleID = "com.typesafe.akka" %% "akka-actor-testkit-typed" % PlayVersion.akkaVersion % Test

  val serverDependencies: Seq[ModuleID] = Seq(
    guice,
    scalaGuice,
    playPac4j,
    sangria,
    sangriaPlayJson,
    caffeine,
    akkaStreamTyped,
    pureconfig,
    scalatestplusPlay,
    akkaStreamTestkit,
    akkaActorTestkitTyped
  )
}
