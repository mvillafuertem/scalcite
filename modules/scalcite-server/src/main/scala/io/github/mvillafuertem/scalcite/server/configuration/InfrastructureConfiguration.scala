package io.github.mvillafuertem.scalcite.server.configuration

import io.github.mvillafuertem.scalcite.server.configuration.properties.{
  CalciteConfigurationProperties,
  H2ConfigurationProperties,
  ScalciteConfigurationProperties
}
import io.github.mvillafuertem.scalcite.server.domain.repository.{ CalciteRepository, ErrorsRepository, QueriesRepository }
import io.github.mvillafuertem.scalcite.server.infrastructure.model.{ ErrorDBO, QueryDBO }
import io.github.mvillafuertem.scalcite.server.infrastructure.repository.{
  RelationalCalciteRepository,
  RelationalErrorsRepository,
  RelationalQueriesRepository
}
import scalikejdbc.{ ConnectionPool, ConnectionPoolSettings }
import zio._

final class InfrastructureConfiguration() {

  ConnectionPool.add(
    Symbol(h2ConfigurationProperties.databaseName),
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

  ConnectionPool.add(
    Symbol(calciteConfigurationProperties.databaseName),
    calciteConfigurationProperties.url,
    calciteConfigurationProperties.user,
    calciteConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = calciteConfigurationProperties.initialSize,
      maxSize = calciteConfigurationProperties.maxSize,
      connectionTimeoutMillis = calciteConfigurationProperties.connectionTimeoutMillis,
      validationQuery = calciteConfigurationProperties.validationQuery,
      driverName = calciteConfigurationProperties.driverName
    )
  )

  lazy val h2ConfigurationProperties: H2ConfigurationProperties = H2ConfigurationProperties()

  lazy val calciteConfigurationProperties: CalciteConfigurationProperties = CalciteConfigurationProperties()

  lazy val scalciteConfigurationProperties: ScalciteConfigurationProperties = ScalciteConfigurationProperties()

  lazy val queriesRepository: QueriesRepository[QueryDBO] =
    RelationalQueriesRepository(h2ConfigurationProperties.databaseName)

  lazy val errorsRepository: ErrorsRepository[ErrorDBO] =
    RelationalErrorsRepository(h2ConfigurationProperties.databaseName)

  lazy val calciteRepository: CalciteRepository =
    RelationalCalciteRepository(calciteConfigurationProperties.databaseName)

}

object InfrastructureConfiguration {

  def apply(): InfrastructureConfiguration =
    new InfrastructureConfiguration()

  type ZInfrastructureConfiguration = Has[InfrastructureConfiguration]

  val h2ConfigurationProperties: URIO[ZInfrastructureConfiguration, H2ConfigurationProperties] =
    ZIO.access(_.get.h2ConfigurationProperties)

  val calciteConfigurationProperties: URIO[ZInfrastructureConfiguration, CalciteConfigurationProperties] =
    ZIO.access(_.get.calciteConfigurationProperties)

  val scalciteConfigurationProperties: URIO[ZInfrastructureConfiguration, ScalciteConfigurationProperties] =
    ZIO.access(_.get.scalciteConfigurationProperties)

  val queriesRepository: URIO[ZInfrastructureConfiguration, QueriesRepository[QueryDBO]] =
    ZIO.access(_.get.queriesRepository)

  val errorsRepository: URIO[ZInfrastructureConfiguration, ErrorsRepository[ErrorDBO]] =
    ZIO.access(_.get.errorsRepository)

  val calciteRepository: URIO[ZInfrastructureConfiguration, CalciteRepository] =
    ZIO.access(_.get.calciteRepository)

  val live: ULayer[ZInfrastructureConfiguration] =
    ZLayer.succeed[InfrastructureConfiguration](InfrastructureConfiguration())

}
