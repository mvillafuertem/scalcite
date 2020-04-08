package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.configuration.properties.{CalciteConfigurationProperties, H2ConfigurationProperties, ScalciteConfigurationProperties}
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalCalciteRepository.CalciteRepo
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalErrorsRepository.ErrorsRepo
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepository.QueriesRepo
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.{RelationalCalciteRepository, RelationalErrorsRepository, RelationalQueriesRepository}
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}
import zio.{ULayer, ZLayer}

import scala.concurrent.ExecutionContext

trait InfrastructureConfiguration {

  implicit val executionContext: ExecutionContext

  ConnectionPool.add(Symbol(h2ConfigurationProperties.databaseName),
    h2ConfigurationProperties.url,
    h2ConfigurationProperties.user,
    h2ConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = h2ConfigurationProperties.initialSize,
      maxSize = h2ConfigurationProperties.maxSize,
      connectionTimeoutMillis = h2ConfigurationProperties.connectionTimeoutMillis,
      validationQuery = h2ConfigurationProperties.validationQuery,
      driverName = h2ConfigurationProperties.driverName
    )
  )

  ConnectionPool.add(Symbol(calciteConfigurationProperties.databaseName),
    calciteConfigurationProperties.url,
    calciteConfigurationProperties.user,
    calciteConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = calciteConfigurationProperties.initialSize,
      maxSize = calciteConfigurationProperties.maxSize,
      connectionTimeoutMillis = calciteConfigurationProperties.connectionTimeoutMillis,
      validationQuery = calciteConfigurationProperties.validationQuery,
      driverName = calciteConfigurationProperties.driverName)
  )

  lazy val h2ConfigurationProperties: H2ConfigurationProperties = H2ConfigurationProperties()

  lazy val calciteConfigurationProperties: CalciteConfigurationProperties = CalciteConfigurationProperties()

  lazy val scalciteConfigurationProperties: ScalciteConfigurationProperties = ScalciteConfigurationProperties()

  val queriesRepositoryLayer: ULayer[QueriesRepo] =
    ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
      RelationalQueriesRepository.live

  val errorsRepositoryLayer: ULayer[ErrorsRepo] =
    ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
      RelationalErrorsRepository.live

  val calciteRepositoryLayer: ULayer[CalciteRepo] =
    ZLayer.succeed(calciteConfigurationProperties.databaseName) >>>
    RelationalCalciteRepository.live

}
