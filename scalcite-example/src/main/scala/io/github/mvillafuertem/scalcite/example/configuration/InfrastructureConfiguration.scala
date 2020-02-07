package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.configuration.properties.{CalciteConfigurationProperties, H2ConfigurationProperties, ScalciteConfigurationProperties}
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.{RelationalCalciteRepository, RelationalQueriesRepository}

import scala.concurrent.ExecutionContext

trait InfrastructureConfiguration {

  implicit val executionContext: ExecutionContext

  lazy val h2ConfigurationProperties: H2ConfigurationProperties = H2ConfigurationProperties()

  lazy val calciteConfigurationProperties: CalciteConfigurationProperties = CalciteConfigurationProperties()

  lazy val scalciteConfigurationProperties: ScalciteConfigurationProperties = ScalciteConfigurationProperties()

  lazy val calciteRepository = new RelationalCalciteRepository(calciteConfigurationProperties.databaseName)

  lazy val queriesRepository = new RelationalQueriesRepository(h2ConfigurationProperties.databaseName)
}
