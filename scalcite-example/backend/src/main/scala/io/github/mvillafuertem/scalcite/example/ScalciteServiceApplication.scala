package io.github.mvillafuertem.scalcite.example
import io.github.mvillafuertem.scalcite.example.configuration.ScalciteServiceConfiguration
import zio.logging.log
import zio.logging.Logging
import zio.{UIO, ZIO, ZManaged}

import scala.concurrent.ExecutionContext

/**
 * @author Miguel Villafuerte
 */
object ScalciteServiceApplication extends ScalciteServiceConfiguration with zio.App {

  val env = Logging.console((_, logEntry) => logEntry)

  override implicit val executionContext: ExecutionContext = platform.executor.asEC

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    ZManaged
      .make(actorSystem)(sys => UIO.succeed(sys.terminate()).ignore)
      .use(
        actorSystem =>
          for {
            _ <- httpServer(actorSystem)
          } yield 0
      )
      .foldM(e => log.throwable("", e).as(1), _ => UIO.effectTotal(0))
      .provideLayer(env)
}
