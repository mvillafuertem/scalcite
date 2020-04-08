package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.application.ErrorsService.ErrorsApp
import io.github.mvillafuertem.scalcite.example.application.QueriesService.QueriesApp
import io.github.mvillafuertem.scalcite.example.application.ScalcitePerformer.ScalciteApp
import io.github.mvillafuertem.scalcite.example.application.{ErrorsService, QueriesService, ScalcitePerformer}
import zio.ULayer

trait ApplicationConfiguration {
  self: InfrastructureConfiguration =>

  val queriesApplicationLayer: ULayer[QueriesApp] =
    queriesRepositoryLayer >>> QueriesService.live

  val errorsApplicationLayer: ULayer[ErrorsApp] =
    errorsRepositoryLayer >>> ErrorsService.live

  val scalciteApplicationLayer: ULayer[ScalciteApp] =
    (queriesApplicationLayer ++ calciteRepositoryLayer) >>>
      ScalcitePerformer.live

}
