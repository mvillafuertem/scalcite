package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.application.QueriesService
import io.github.mvillafuertem.scalcite.example.application.QueriesService.ZQueriesApplication
import io.github.mvillafuertem.scalcite.example.configuration.ActorSystemConfiguration.ZActorSystemConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.AkkaHttpConfiguration.ZMaterializer
import io.github.mvillafuertem.scalcite.example.configuration.ApiConfiguration.ZApiConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.ApplicationConfiguration.ZApplicationConfiguration
import zio.{TaskLayer, ULayer, ZLayer}


trait ScalciteServiceConfiguration {

  private val materializerLayer: TaskLayer[ZMaterializer] =
    ActorSystemConfiguration.live >>>
      AkkaHttpConfiguration.materializerLive

  private val queriesApplicationLayer: ULayer[ZQueriesApplication] =
    (InfrastructureConfiguration.queriesRepositoryLayer ++
      InfrastructureConfiguration.errorsRepositoryLayer) >>>
    QueriesService.live

  private val applicationConfigurationLayer: ULayer[ZApplicationConfiguration] =
    (InfrastructureConfiguration.live ++ queriesApplicationLayer) >>>
      ApplicationConfiguration.live

  private val apiConfigurationLayer: TaskLayer[ZApiConfiguration] =
    (applicationConfigurationLayer ++
      materializerLayer) >>>
      ApiConfiguration.live

  val ZScalciteEnv: ZLayer[Any, Throwable, ZApiConfiguration with ZActorSystemConfiguration] =
    apiConfigurationLayer ++ ActorSystemConfiguration.live

}