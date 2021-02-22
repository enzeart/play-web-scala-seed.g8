import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object $name;format="space,Camel"$Dependencies {

  val playPac4j: ModuleID = "org.pac4j" %% "play-pac4j" % "$play_pac4j_version$"
  val scalaGuice: ModuleID = "net.codingwell" %% "scala-guice" % "$scala_guice_version$"
  val sangria: ModuleID = "org.sangria-graphql" %% "sangria" % "$sangria_version$"
  val sangriaPlayJson: ModuleID = "org.sangria-graphql" %% "sangria-play-json" % "$sangria_version$"
  val akkaStreamTyped: ModuleID = "com.typesafe.akka" %% "akka-stream-typed" % PlayVersion.akkaVersion

  val rootDependencies: Seq[ModuleID] = Seq(
    guice,
    scalaGuice,
    playPac4j,
    sangria,
    sangriaPlayJson,
    caffeine,
    akkaStreamTyped
  )
}
