import org.apache.commons.io.FileUtils
import play.sbt.{PlayConsoleInteractionMode, PlayInteractionMode}

import java.io.*
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Using

object AppInteractionMode {

  val Default: AppInteractionMode = {
    val terminatorFile = new File(FileUtils.getUserDirectory, ".AppInteractionMode")
    val environmentVariable = "APP_INTERACTION_MODE_CHILD_PROCESS"
    new AppInteractionMode(terminatorFile, environmentVariable)
  }
}

class AppInteractionMode(val terminatorFile: File, val environmentVariable: String) extends PlayInteractionMode {

  override def doWithoutEcho(f: => Unit): Unit = PlayConsoleInteractionMode.doWithoutEcho(f)

  override def waitForCancel(): Unit = {
    Future {
      PlayConsoleInteractionMode.waitForCancel()
      terminatorFile.delete()
    }

    Using(FileSystems.getDefault.newWatchService())(watchService => {
      try {
        if (!sys.env.contains(environmentVariable)) terminatorFile.createNewFile()
        terminatorFile.toPath.getParent.register(watchService, ENTRY_DELETE)
        while (terminatorFile.exists()) watchService.take()
      } finally terminatorFile.delete()
    })
  }
}
