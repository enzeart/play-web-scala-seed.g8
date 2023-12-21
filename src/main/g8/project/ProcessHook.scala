import ProcessHook.{Destroy, Terminator}
import play.sbt.PlayRunHook

import scala.sys.process.Process

object ProcessHook {

  type Terminator = Process => Unit

  val Destroy: Terminator = _.destroy()

  val ExitValue: Terminator = _.exitValue()
}

class ProcessHook(process: => Process, terminator: Terminator = Destroy) extends PlayRunHook {

  private var currentProcess: Option[Process] = None

  override def afterStarted(): Unit = currentProcess = Option(process)

  override def afterStopped(): Unit = currentProcess.foreach(terminator)
}
