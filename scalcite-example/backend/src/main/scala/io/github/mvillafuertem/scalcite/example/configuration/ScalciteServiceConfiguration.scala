package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.configuration.ActorSystemConfiguration.ZActorSystemConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.AkkaConfiguration.ZAkkaConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.ApiConfiguration.ZApiConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.ApplicationConfiguration.ZApplicationConfiguration
import zio.{TaskLayer, ULayer}


trait ScalciteServiceConfiguration {

  private val applicationConfigurationLayer: ULayer[ZApplicationConfiguration] =
    InfrastructureConfiguration.live >>>
      ApplicationConfiguration.live

  private val akkaConfigurationLayer: TaskLayer[ZAkkaConfiguration] =
    InfrastructureConfiguration.live >>>
      AkkaConfiguration.live

  private val apiConfigurationLayer: TaskLayer[ZApiConfiguration] =
    (applicationConfigurationLayer
      ++ akkaConfigurationLayer) >>>
      ApiConfiguration.live

  private val akkaSystemLayer: TaskLayer[ZActorSystemConfiguration] =
    InfrastructureConfiguration.live >>>
      ActorSystemConfiguration.live

  val ZScalciteEnv: TaskLayer[ZApiConfiguration with ZActorSystemConfiguration with ZAkkaConfiguration] =
    apiConfigurationLayer ++ akkaSystemLayer ++ akkaConfigurationLayer

}