import sbt.Keys.TaskStreams
import sbt.State

import scala.sys.process._

object Infrastructure {

  val shell: Seq[String] =
    if (sys.props("os.name").contains("Windows")) Vector("cmd", "/c")
    else Vector("bash", "-c")

  def up(s: State): Unit = {
    val dockerComposeUp = shell :+ s"docker-compose -f modules/scalcite-example/docker-compose.yml up -d"
    if (dockerComposeUp.! == 0) s.log.success(s"Infrastructure was started")
    else s.log.error("Infrastructure was not able to start up")
  }

  def down(s: State): Unit = {
    val dockerComposeDown = shell :+ s"docker-compose -f modules/scalcite-example/docker-compose.yml down"
    if (dockerComposeDown.! == 0) s.log.success("Infrastructure was stopped")
    else s.log.error("Infrastructure was not able to shut down properly")
  }

}
