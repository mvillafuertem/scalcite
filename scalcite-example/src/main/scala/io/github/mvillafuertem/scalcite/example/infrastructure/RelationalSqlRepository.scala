package io.github.mvillafuertem.scalcite.example.infrastructure

import akka.NotUsed
import akka.stream.scaladsl.Source
import io.github.mvillafuertem.scalcite.example.configuration.H2ConfigurationProperties
import io.github.mvillafuertem.scalcite.example.domain.model.ScalciteSql
import io.github.mvillafuertem.scalcite.example.domain.repository.SqlRepository
import scalikejdbc.streams._
import scalikejdbc._
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings, DB, SQL}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * @author Miguel Villafuerte
  */
final class RelationalSqlRepository(h2ConfigurationProperties: H2ConfigurationProperties) extends SqlRepository[Source] {

  ConnectionPool.add('sqldb,
    h2ConfigurationProperties.url,
    h2ConfigurationProperties.user,
    h2ConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = h2ConfigurationProperties.initialSize,
      maxSize = h2ConfigurationProperties.maxSize,
      connectionTimeoutMillis = h2ConfigurationProperties.connectionTimeoutMillis,
      validationQuery = h2ConfigurationProperties.validationQuery,
      driverName = h2ConfigurationProperties.driverName)
  )

  override def findById(id: Long) = Source.fromPublisher[Map[String, Any]] {
    NamedDB('sqldb) readOnlyStream {
      SQL("")
        .map(_.toMap())
        .iterator()
        .withDBSessionForceAdjuster(session => {
          println(session)
          session.connection.setAutoCommit(true)
        })
    }
  }

  override def insert(scalciteSql: ScalciteSql): Source[Map[String, Any], NotUsed] = Source.fromPublisher[Map[String, Any]] {
    DB readOnlyStream {

      sql"INSERT INTO scalcitesql VALUES (${scalciteSql.id}, ${scalciteSql.sql})"
        .map(_.toMap())
        .iterator()
        .withDBSessionForceAdjuster(session => {
          println(session)
          //session.connection.setAutoCommit(true)
        })
    }
  }

  def insert2(scalciteSql: ScalciteSql) =

    NamedDB('sqldb) autoCommit { implicit session =>
      sql"INSERT INTO scalcitesql VALUES (${scalciteSql.id}, ${scalciteSql.sql})"
        .update.apply()
    }




  override def delete() = Source.fromPublisher[Map[String, Any]] {
    DB readOnlyStream {
      SQL("")
        .map(_.toMap())
        .iterator()
        .withDBSessionForceAdjuster(session => {
          println(session)
          session.connection.setAutoCommit(true)
        })
    }
  }

}

//object RelationalSqlRepository extends SQLSyntaxSupport[ScalciteSql] {
//
//  def streamBy(condition: SQLSyntax): StreamReadySQL[ScalciteSql] = {
//    withSQL {
//      select.from(RelationalSqlRepository as).where(condition)
//    }.map(_.toMap()).iterator()
//  }
//
//
//}
