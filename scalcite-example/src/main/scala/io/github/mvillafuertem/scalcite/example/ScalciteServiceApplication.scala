package io.github.mvillafuertem.scalcite.example
import io.github.mvillafuertem.scalcite.example.configuration.ScalciteServiceConfiguration
import zio.{UIO, ZIO, ZManaged}

import scala.concurrent.ExecutionContext

/**
  * @author Miguel Villafuerte
  */
object ScalciteServiceApplication extends ScalciteServiceConfiguration
  with zio.App  {


 override implicit val executionContext: ExecutionContext = platform.executor.asEC

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    ZManaged.make(actorSystem)(sys => UIO.succeed(sys.terminate()).ignore)
      .use(
        actorSystem =>
          for {
            _ <- httpServer(actorSystem)
          } yield 0).fold(_ => 1, _ => 0)



}
