import GitSubmoduleServiceHook.{ServiceContext, ServiceTerminationHook}
import org.eclipse.jgit.storage.file.FileBasedConfig
import org.eclipse.jgit.util.FS
import play.sbt.PlayRunHook
import sbt.File

import java.io.OutputStream
import java.util.concurrent.atomic.AtomicReference
import scala.sys.process.{BasicIO, Process}

object GitSubmoduleServiceHook {

  type ServiceTerminationHook = ServiceContext => Unit

  val PlayConsoleInteractionModeTerminationHook: ServiceTerminationHook = (s: ServiceContext) =>
    s.stdin.foreach(in => { in.write(13); in.write(13); in.flush() })

  final case class ServiceContext(process: Option[Process] = None, stdin: Option[OutputStream] = None)

  def apply(
      repositoryRoot: File,
      submoduleName: String,
      command: Seq[String],
      terminationHook: ServiceTerminationHook = PlayConsoleInteractionModeTerminationHook
  ): PlayRunHook = {
    new GitSubmoduleServiceHook(repositoryRoot, submoduleName, command, terminationHook)
  }
}

private final class GitSubmoduleServiceHook(
    repositoryRoot: File,
    submoduleName: String,
    command: Seq[String],
    terminationHook: ServiceTerminationHook
) extends PlayRunHook {

  private val serviceContext: AtomicReference[ServiceContext] = new AtomicReference(ServiceContext())

  override def afterStarted(): Unit = {
    val config = new FileBasedConfig(new File(repositoryRoot, ".gitmodules"), FS.detect())
    config.load()
    val directory = Option(config.getString("submodule", submoduleName, "path")).map(new File(_).getCanonicalFile)

    directory.foreach { d =>
      val processBuilder = Process(command, Option(d))
      val process = processBuilder.run(BasicIO.standard(in => serviceContext.updateAndGet(_.copy(stdin = Option(in)))))
      serviceContext.updateAndGet(_.copy(process = Option(process)))
    }
  }

  override def afterStopped(): Unit = {
    val s = serviceContext.get()
    terminationHook(s)
    s.process.foreach(p => p.exitValue())
  }
}
