import play.sbt.PlayImport._
import sbt._

object Dependencies {

  lazy val playPac4jVersion: String = "$playPac4jVersion$"

  val playPac4j: ModuleID = "org.pac4j" %% "play-pac4j" % playPac4jVersion
  val scalaGuice: ModuleID = "net.codingwell" %% "scala-guice" % "$scalaGuiceVersion$"

  val rootDependencies: Seq[ModuleID] = Seq(
    guice,
    scalaGuice,
    playPac4j,
    caffeine
  )
}