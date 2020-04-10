package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.configuration.properties.{CalciteConfigurationProperties, H2ConfigurationProperties, ScalciteConfigurationProperties}
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalCalciteRepository.ZCalciteRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalErrorsRepository.ZErrorsRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepository.ZQueriesRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.{RelationalCalciteRepository, RelationalErrorsRepository, RelationalQueriesRepository}
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}
import zio._

trait InfrastructureConfiguration {

  val connectionPoolH2 = Task(ConnectionPool.add(Symbol(h2ConfigurationProperties.databaseName),
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
  ))

  val connectionPoolCalcite: Task[Unit] = Task(ConnectionPool.add(Symbol(calciteConfigurationProperties.databaseName),
    calciteConfigurationProperties.url,
    calciteConfigurationProperties.user,
    calciteConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = calciteConfigurationProperties.initialSize,
      maxSize = calciteConfigurationProperties.maxSize,
      connectionTimeoutMillis = calciteConfigurationProperties.connectionTimeoutMillis,
      validationQuery = calciteConfigurationProperties.validationQuery,
      driverName = calciteConfigurationProperties.driverName)
  ))

  private lazy val h2ConfigurationProperties: H2ConfigurationProperties =
    H2ConfigurationProperties()

  private lazy val calciteConfigurationProperties: CalciteConfigurationProperties =
    CalciteConfigurationProperties()

  lazy val scalciteConfigurationProperties: ScalciteConfigurationProperties =
    ScalciteConfigurationProperties()

  val calciteRepositoryLayer: ULayer[ZCalciteRepository] =
    ZLayer.succeed(calciteConfigurationProperties.databaseName) >>>
      RelationalCalciteRepository.live

  val errorsRepositoryLayer: ULayer[ZErrorsRepository] =
    ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
      RelationalErrorsRepository.live

  val queriesRepositoryLayer: ULayer[ZQueriesRepository] =
    ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
      RelationalQueriesRepository.live

  type ZInfrastructureConfiguration = ZCalciteRepository with ZErrorsRepository with ZQueriesRepository

  val live: ULayer[ZInfrastructureConfiguration] =
    calciteRepositoryLayer ++
      errorsRepositoryLayer ++
      queriesRepositoryLayer

}

object InfrastructureConfiguration extends InfrastructureConfiguration
