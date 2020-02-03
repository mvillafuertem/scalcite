package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.configuration.properties.{CalciteConfigurationProperties, H2ConfigurationProperties}
import io.github.mvillafuertem.scalcite.example.infrastructure.{RelationalScalciteRepository, RelationalSqlRepository}

trait InfrastructureConfiguration {

  lazy val h2ConfigurationProperties: H2ConfigurationProperties = H2ConfigurationProperties()

  lazy val calciteConfigurationProperties: CalciteConfigurationProperties = CalciteConfigurationProperties()

  lazy val scalciteConfigurationProperties: ScalciteConfigurationProperties = ScalciteConfigurationProperties()

  lazy val scalciteRepository = new RelationalScalciteRepository(calciteConfigurationProperties)

  lazy val sqlRepository = new RelationalSqlRepository(h2ConfigurationProperties)

}

object InfrastructureConfiguration extends InfrastructureConfiguration
