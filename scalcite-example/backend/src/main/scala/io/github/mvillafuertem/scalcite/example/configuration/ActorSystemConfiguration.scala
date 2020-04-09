package io.github.mvillafuertem.scalcite.example.configuration

import akka.Done
import akka.actor.BootstrapSetup
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import io.github.mvillafuertem.scalcite.example.ScalciteServiceApplication.platform
import io.github.mvillafuertem.scalcite.example.configuration.InfrastructureConfiguration.ZInfrastructureConfiguration
import zio._

import scala.concurrent.ExecutionContext

object ActorSystemConfiguration {

  type ZActorSystemConfiguration = Has[ActorSystem[Done]]

  lazy val executionContext: Task[ExecutionContext] = Task(platform.executor.asEC)

  private lazy val actorSystem: ZIO[ZInfrastructureConfiguration, Throwable, ActorSystem[Done]] =
    for {
      scalciteConfigurationProperties <- InfrastructureConfiguration.scalciteConfigurationProperties
      executionContext <- executionContext
      actorSystem <- Task(
        ActorSystem[Done](
          Behaviors.setup[Done] { context =>
            context.setLoggerName(this.getClass)
            context.log.info(s"Starting ${scalciteConfigurationProperties.name}... ${"BuildInfo.toJson"}")
            Behaviors.receiveMessage {
              case Done =>
                context.log.error(s"Server could not start!")
                Behaviors.stopped
            }
          },
          scalciteConfigurationProperties.name.toLowerCase(),
          BootstrapSetup().withDefaultExecutionContext(executionContext)
        )
      )
    } yield actorSystem

  val live: ZLayer[ZInfrastructureConfiguration, Throwable, ZActorSystemConfiguration] = ZLayer
    .fromAcquireRelease(actorSystem)(
      actorSystem => UIO.succeed(actorSystem.terminate()).ignore)
}
