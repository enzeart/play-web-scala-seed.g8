import play.sbt.PlayImport._
import sbt._

object $name;format="space,Camel"$Dependencies {

  val playPac4j: ModuleID = "org.pac4j" %% "play-pac4j" % "$playPac4jVersion$"
  val scalaGuice: ModuleID = "net.codingwell" %% "scala-guice" % "$scalaGuiceVersion$"
  val sangria: ModuleID = "org.sangria-graphql" %% "sangria" % "$sangriaVersion$"
  val sangriaPlayJson: ModuleID = "org.sangria-graphql" %% "sangria-play-json" % "$sangriaVersion$"
  val sangriaAkkaStreams: ModuleID = "org.sangria-graphql" %% "sangria-akka-streams" % "$sangriaAkkaStreamsVersion$"

  val rootDependencies: Seq[ModuleID] = Seq(
    guice,
    scalaGuice,
    playPac4j,
    sangria,
    sangriaPlayJson,
    caffeine,
    sangriaAkkaStreams
  )
}