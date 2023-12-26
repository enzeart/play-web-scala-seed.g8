import org.apache.commons.io.FileUtils
import play.sbt.{PlayConsoleInteractionMode, PlayInteractionMode}

import java.io._
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Using

object AppInteractionMode {

  val Default: AppInteractionMode = {
    val terminatorVariable = "APP_INTERACTION_MODE_TERMINATOR_FILE"
    val terminatorFile = sys.env
      .get(terminatorVariable)
      .map(new File(_))
      .getOrElse(new File(FileUtils.getUserDirectory, s".AppInteractionModeTerminator_\${UUID.randomUUID()}"))
      .getCanonicalFile
    new AppInteractionMode(terminatorFile, terminatorVariable)
  }
}

class AppInteractionMode private (val terminatorFile: File, val terminatorVariable: String)
    extends PlayInteractionMode {

  override def doWithoutEcho(f: => Unit): Unit = PlayConsoleInteractionMode.doWithoutEcho(f)

  override def waitForCancel(): Unit = {
    Future {
      PlayConsoleInteractionMode.waitForCancel()
      terminatorFile.delete()
    }

    Using(FileSystems.getDefault.newWatchService())(watchService => {
      try {
        if (!sys.env.contains(terminatorVariable)) terminatorFile.createNewFile()
        terminatorFile.toPath.getParent.register(watchService, ENTRY_DELETE)
        while (terminatorFile.exists()) watchService.take().reset()
      } finally terminatorFile.delete()
    })
  }
}
