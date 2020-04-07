package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.application.{ErrorsService, QueriesService, ScalcitePerformer}
import io.github.mvillafuertem.scalcite.example.domain.{ErrorsApplication, QueriesApplication, ScalciteApplication}

trait ApplicationConfiguration {
  self: InfrastructureConfiguration =>

  lazy val errorsApplication: ErrorsApplication = ErrorsService(errorsRepository)

  lazy val queriesApplication: QueriesApplication = QueriesService(queriesRepository)

  lazy val scalciteApplication: ScalciteApplication = ScalcitePerformer(calciteRepository, queriesApplication)

}
