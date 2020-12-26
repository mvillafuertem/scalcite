package io.github.mvillafuertem.scalcite.server.configuration

import ActorSystemConfiguration.ZActorSystemConfiguration
import AkkaHttpConfiguration.{ ZAkkaHttpConfiguration, ZMaterializer }
import ApiConfiguration.ZApiConfiguration
import ApplicationConfiguration.ZApplicationConfiguration
import zio.{ TaskLayer, ULayer, ZLayer }

trait ScalciteServiceConfiguration {

  private val akkaSystemLayer: TaskLayer[ZActorSystemConfiguration] =
    InfrastructureConfiguration.live >>>
      ActorSystemConfiguration.live

  private val akkaHttpConfigurationLayer: TaskLayer[ZAkkaHttpConfiguration] =
    (InfrastructureConfiguration.live ++
      akkaSystemLayer) >>>
      AkkaHttpConfiguration.live

  private val materializerLayer: TaskLayer[ZMaterializer] =
    akkaSystemLayer >>>
      AkkaHttpConfiguration.materializerLive

  private val applicationConfigurationLayer: ULayer[ZApplicationConfiguration] =
    InfrastructureConfiguration.live >>>
      ApplicationConfiguration.live

  private val apiConfigurationLayer: TaskLayer[ZApiConfiguration] =
    (applicationConfigurationLayer ++
      akkaHttpConfigurationLayer ++
      materializerLayer) >>>
      ApiConfiguration.live

  val ZScalciteEnv: ZLayer[Any, Throwable, ZApiConfiguration with ZActorSystemConfiguration with ZAkkaHttpConfiguration] =
    apiConfigurationLayer ++ akkaSystemLayer ++ akkaHttpConfigurationLayer

}
