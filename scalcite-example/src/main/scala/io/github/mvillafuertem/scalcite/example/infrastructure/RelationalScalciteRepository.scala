package io.github.mvillafuertem.scalcite.example.infrastructure

import akka.NotUsed
import akka.stream.scaladsl.Source
import io.circe.Json
import io.circe.scalcite.example.ScalciteTypeImpl
import io.github.mvillafuertem.scalcite.example.configuration.CalciteConfigurationProperties
import io.github.mvillafuertem.scalcite.example.domain.repository.ScalciteRepository
import io.github.mvillafuertem.scalcite.{MapTable, ScalciteTable}
import org.apache.calcite.jdbc.CalciteConnection
import scalikejdbc.streams._
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings, NamedDB, SQL}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * @author Miguel Villafuerte
  */
final class RelationalScalciteRepository(calciteConfigurationProperties: CalciteConfigurationProperties) extends ScalciteRepository[Source] {


  //    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
  //      enabled = true,
  //      singleLineMode = true,
  //    )

  ConnectionPool.add(Symbol("calcitedb"),
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

  override def queryForMap(map: Map[String, Any], sql: String): Source[Map[String, Any], NotUsed] = Source.fromPublisher[Map[String, Any]] {
    NamedDB(Symbol("calcitedb")) readOnlyStream {
      SQL(sql)
        .map(_.toMap())
        .iterator()
        .withDBSessionForceAdjuster(session => {
          println(session)
          session.connection.unwrap(classOf[CalciteConnection])
            .getRootSchema
            .add("person", MapTable(map))
        })
    }
  }

  override def queryForJson(json: Json, sql: String): Source[Map[String, Any], NotUsed] = Source.fromPublisher[Map[String, Any]] {
    NamedDB(Symbol("calcitedb")) readOnlyStream {
      SQL(sql)
        .map(_.toMap)
        .iterator()
        .withDBSessionForceAdjuster(session => {
          session.connection.unwrap(classOf[CalciteConnection])
            .getRootSchema
            .add("person", ScalciteTable(ScalciteTypeImpl(json)))
        })
    }
  }

}

object RelationalScalciteRepository {
  def apply(calciteConfigurationProperties: CalciteConfigurationProperties): RelationalScalciteRepository = new RelationalScalciteRepository(calciteConfigurationProperties)
}
