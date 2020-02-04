package io.github.mvillafuertem.scalcite.example.infrastructure

import akka.NotUsed
import akka.stream.scaladsl.Source
import io.github.mvillafuertem.scalcite.example.configuration.properties.H2ConfigurationProperties
import io.github.mvillafuertem.scalcite.example.domain.model.ScalciteSql
import io.github.mvillafuertem.scalcite.example.domain.repository.QueriesRepository
import org.reactivestreams.Subscriber
import scalikejdbc.streams._
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings, SQL, _}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * @author Miguel Villafuerte
  */
final class RelationalQueriesRepository(h2ConfigurationProperties: H2ConfigurationProperties) extends QueriesRepository[Source] {

  ConnectionPool.add(Symbol("sqldb"),
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
    NamedDB(Symbol("sqldb")) readOnlyStream {
      sql"SELECT * FROM scalcitesql WHERE id = ${id}"
        .map(_.toMap())
        .iterator()
        .withDBSessionForceAdjuster(session => {
          println(session)
          session.connection.setAutoCommit(true)
        })
    }
  }

  override def insert(scalciteSql: ScalciteSql) = Source.fromPublisher[Map[String, Any]] {
    (s: Subscriber[_ >: Map[String, Any]]) => { NamedDB(Symbol("sqldb")) autoCommit  { implicit session =>

      val bool = sql"INSERT INTO scalcitesql VALUES (${scalciteSql.id}, ${scalciteSql.sql})"
        .update()
        .apply()
      println(s"RESULTR $bool" )
      if (bool == 1) {
        s.onNext(Map("SQL" -> scalciteSql.sql))
        s.onComplete()
      } else s.onComplete()
    }
    }
  }


  override def delete() = Source.fromPublisher[Map[String, Any]] {
    NamedDB(Symbol("sqldb")) readOnlyStream {
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
