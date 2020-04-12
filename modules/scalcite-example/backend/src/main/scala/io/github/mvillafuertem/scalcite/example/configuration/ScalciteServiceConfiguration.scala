package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.configuration.ActorSystemConfiguration.ZActorSystemConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.AkkaHttpConfiguration.{ZAkkaHttpConfiguration, ZMaterializer}
import io.github.mvillafuertem.scalcite.example.configuration.ApiConfiguration.ZApiConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.ApplicationConfiguration.ZApplicationConfiguration
import zio.{TaskLayer, ULayer, ZLayer}


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