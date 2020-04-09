package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.configuration.ActorSystemConfiguration.ZAkkaSystemConfiguration
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

  private val akkaSystemLayer: TaskLayer[ZAkkaSystemConfiguration] =
    InfrastructureConfiguration.live >>>
      ActorSystemConfiguration.live

  val ZScalciteEnv: TaskLayer[ZApiConfiguration with ZAkkaSystemConfiguration with ZAkkaConfiguration] =
    apiConfigurationLayer ++ akkaSystemLayer ++ akkaConfigurationLayer

}