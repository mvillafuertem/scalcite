package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.infrastructure.{RelationalScalciteRepository, RelationalSqlRepository}


object ScalciteServiceConfiguration {

  lazy val h2ConfigurationProperties = H2ConfigurationProperties()

  lazy val calciteConfigurationProperties = CalciteConfigurationProperties()

  lazy val scalciteRepository = new RelationalScalciteRepository(calciteConfigurationProperties)

  lazy val sqlRepository = new RelationalSqlRepository(h2ConfigurationProperties)

}
