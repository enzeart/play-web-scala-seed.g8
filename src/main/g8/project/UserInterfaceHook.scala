import play.sbt.PlayRunHook
import sbt.{File, Logger}

import scala.sys.process.Process

object UserInterfaceHook {

  def apply(command: Seq[String], directory: File, log: Logger): PlayRunHook = {
    new UserInterfaceHook(command, directory, log)
  }
}

private class UserInterfaceHook(command: Seq[String], directory: File, log: Logger) extends PlayRunHook {

  private val processHook = new ProcessHook(Process(command, directory).run(), ProcessHook.Destroy)

  override def afterStarted(): Unit = {
    if (directory.exists()) processHook.afterStarted()
    else log.warn(s"UI directory ($directory) not found. Skipping command execution.")
  }

  override def afterStopped(): Unit = processHook.afterStopped()
}
