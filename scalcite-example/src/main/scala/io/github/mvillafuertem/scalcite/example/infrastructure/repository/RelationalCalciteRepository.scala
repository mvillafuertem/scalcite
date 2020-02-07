package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import io.circe.Json
import io.circe.scalcite.example.ScalciteTypeImpl
import io.github.mvillafuertem.scalcite.example.domain.repository.CalciteRepository
import io.github.mvillafuertem.scalcite.{MapTable, ScalciteTable}
import org.apache.calcite.jdbc.CalciteConnection
import scalikejdbc._
import scalikejdbc.streams._
import zio.interop.reactiveStreams._
import zio.stream

import scala.concurrent.ExecutionContext

/**
 * @author Miguel Villafuerte
 */
final class RelationalCalciteRepository(databaseName: String)(
    implicit executionContext: ExecutionContext
) extends CalciteRepository {

  override def queryForMap(map: collection.Map[String, Any], query: String): stream.Stream[Throwable, collection.Map[String, Any]] =
    (NamedDB(Symbol(databaseName)) readOnlyStream {
      SQL(query)
        .map(_.toMap())
        .iterator()
        .withDBSessionForceAdjuster(session => {
          session.connection
            .unwrap(classOf[CalciteConnection])
            .getRootSchema
            .add("scalcite", MapTable(map))
        })

    }).toStream()

  override def queryForJson(json: Json, query: String): stream.Stream[Throwable, collection.Map[String, Any]] =
    (NamedDB(Symbol(databaseName)) readOnlyStream {
      SQL(query)
        .map(_.toMap)
        .iterator()
        .withDBSessionForceAdjuster(session => {
          session.connection
            .unwrap(classOf[CalciteConnection])
            .getRootSchema
            .add("scalcite", ScalciteTable(ScalciteTypeImpl(json)))
        })

    }).toStream()

}

object RelationalCalciteRepository {
  def apply(databaseName: String)(implicit executionContext: ExecutionContext): RelationalCalciteRepository =
    new RelationalCalciteRepository(databaseName)(executionContext)
}
