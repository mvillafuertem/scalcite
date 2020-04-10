package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.application.ErrorsService.ZErrorsApplication
import io.github.mvillafuertem.scalcite.example.application.QueriesService.ZQueriesApplication
import io.github.mvillafuertem.scalcite.example.application.ScalcitePerformer.ZScalciteApplication
import io.github.mvillafuertem.scalcite.example.application.{ErrorsService, QueriesService, ScalcitePerformer}
import io.github.mvillafuertem.scalcite.example.configuration.InfrastructureConfiguration.ZInfrastructureConfiguration
import zio.ZLayer


trait ApplicationConfiguration {

  type ZApplicationConfiguration =
    ZScalciteApplication with
      ZQueriesApplication with
      ZErrorsApplication

  val live: ZLayer[ZQueriesApplication with ZInfrastructureConfiguration, Nothing, ZApplicationConfiguration] =
    ScalcitePerformer.live ++
      QueriesService.live ++
      ErrorsService.live

}

object ApplicationConfiguration extends ApplicationConfiguration


