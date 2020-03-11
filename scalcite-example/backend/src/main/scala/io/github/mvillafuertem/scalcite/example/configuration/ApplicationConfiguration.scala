package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.application.ScalcitePerformer
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication

trait ApplicationConfiguration {
  self: InfrastructureConfiguration =>

  lazy val scalciteApplication: ScalciteApplication = ScalcitePerformer(calciteRepository, queriesRepository)

}
