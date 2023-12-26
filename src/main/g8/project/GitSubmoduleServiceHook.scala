import GitSubmoduleServiceHook.SharedContext
import ProcessHook.ExitValue
import org.eclipse.jgit.storage.file.FileBasedConfig
import org.eclipse.jgit.util.FS
import play.sbt.{PlayInteractionMode, PlayRunHook}
import sbt.{File, Logger}

import scala.sys.process.Process

object GitSubmoduleServiceHook {

  object SharedContext {

    def forInteractionMode(
        interactionMode: PlayInteractionMode = AppInteractionMode.Default,
        extraEnv: Seq[(String, String)] = Seq.empty,
        logger: Logger
    ): SharedContext = {
      interactionMode match {
        case app: AppInteractionMode =>
          val env = extraEnv :+ (app.terminatorVariable, app.terminatorFile.toString)
          SharedContext(extraEnv = env, logger = logger)
        case _ => SharedContext(extraEnv = extraEnv, logger = logger)
      }
    }
  }

  case class SharedContext(extraEnv: Seq[(String, String)] = Seq.empty, logger: Logger)

  def apply(
      repositoryRoot: File,
      submoduleName: String,
      command: Seq[String],
      extraEnv: Seq[(String, String)] = Seq.empty
  )(implicit ctx: SharedContext): PlayRunHook = {
    new GitSubmoduleServiceHook(repositoryRoot, submoduleName, command, extraEnv)
  }
}

final private class GitSubmoduleServiceHook(
    repositoryRoot: File,
    submoduleName: String,
    command: Seq[String],
    extraEnv: Seq[(String, String)]
)(implicit ctx: SharedContext)
    extends PlayRunHook {

  private val config = {
    val config = new FileBasedConfig(new File(repositoryRoot, ".gitmodules"), FS.detect())
    config.load()
    config
  }

  private val directory = Option(config.getString("submodule", submoduleName, "path")).map(new File(_).getCanonicalFile)

  private val submoduleGitFile = directory.map(new File(_, ".git"))

  private val processHook =
    directory.map(d => new ProcessHook(Process(command, d, extraEnv ++ ctx.extraEnv: _*).run(), ExitValue))

  override def afterStarted(): Unit = {
    if (submoduleGitFile.exists(_.exists)) processHook.foreach(_.afterStarted())
    else
      ctx.logger.warn(
        s"Submodule '\$submoduleName' is not initialized (.git file not found). Skipping command execution."
      )
  }

  override def afterStopped(): Unit = processHook.foreach(_.afterStopped())
}
