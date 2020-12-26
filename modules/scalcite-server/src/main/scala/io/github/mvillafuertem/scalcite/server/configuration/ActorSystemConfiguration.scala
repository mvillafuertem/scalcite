package io.github.mvillafuertem.scalcite.server.configuration

import akka.Done
import akka.actor.BootstrapSetup
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import io.github.mvillafuertem.scalcite.server.ScalciteServiceApplication.platform
import InfrastructureConfiguration.ZInfrastructureConfiguration
import io.github.mvillafuertem.scalcite.BuildInfoScalcite
import zio._

import scala.concurrent.ExecutionContext

object ActorSystemConfiguration {

  type ZActorSystemConfiguration = Has[ActorSystem[_]]

  lazy val executionContext: Task[ExecutionContext] = Task(platform.executor.asEC)

  private lazy val actorSystem: ZIO[ZInfrastructureConfiguration, Throwable, ActorSystem[_]] =
    for {
      scalciteConfigurationProperties <- InfrastructureConfiguration.scalciteConfigurationProperties
      executionContext                <- executionContext
      actorSystem <- Task(
                      ActorSystem[Done](
                        Behaviors.setup[Done] { context =>
                          context.setLoggerName(this.getClass)
                          context.log.info(s"Starting ${scalciteConfigurationProperties.name}... ${BuildInfoScalcite.toJson}")
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
    .fromAcquireRelease(actorSystem)(actorSystem => UIO.succeed(actorSystem.terminate()).ignore)
}
