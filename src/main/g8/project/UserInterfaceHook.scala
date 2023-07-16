import play.sbt.PlayRunHook
import sbt.File

import scala.sys.process.Process

object UserInterfaceHook {

  def apply(directory: File): PlayRunHook = {
    new UserInterfaceHook(directory)
  }
}

private class UserInterfaceHook(directory: File) extends PlayRunHook {

  private var npmStart: Option[Process] = None

  override def afterStarted(): Unit = {
    if (directory.exists()) {
      npmStart = Option(Process("npm" :: "run" :: "start" :: Nil, directory).run())
    }
  }

  override def afterStopped(): Unit = npmStart.foreach(_.destroy())
}
