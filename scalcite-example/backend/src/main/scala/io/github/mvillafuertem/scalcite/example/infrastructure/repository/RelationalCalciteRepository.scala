package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import java.sql.SQLException

import io.circe.Json
import io.circe.scalcite.table.ScalciteCirceTable
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.Unknown
import io.github.mvillafuertem.scalcite.example.domain.repository.CalciteRepository
import org.apache.calcite.jdbc.CalciteConnection
import scalikejdbc.streams._
import scalikejdbc.{WrappedResultSet, _}
import zio.interop.reactivestreams._
import zio.stream.ZStream
import zio.{Task, stream}

import scala.concurrent.ExecutionContext

/**
 * @author Miguel Villafuerte
 */
final class RelationalCalciteRepository(databaseName: String)(
    implicit executionContext: ExecutionContext
) extends CalciteRepository {

  override def queryForMap(map: collection.Map[String, Any],
                           query: String): stream.Stream[Throwable, collection.Map[String, Any]] =
    (NamedDB(Symbol(databaseName)) readOnlyStream {
      SQL(query)
        .map(_.toMap())
        .iterator()
        .withDBSessionForceAdjuster(session => {
          session.connection
            .unwrap(classOf[CalciteConnection])
            .getRootSchema
            .add("scalcite", ScalciteMapType(map))
        })

    }).toStream()

  override def queryForJson(json: Json, query: String): stream.Stream[ScalciteError, Json] =
    ZStream.fromEffect {
        Task.effect(NamedDB(Symbol(databaseName)) autoCommit { implicit session =>
          session.connection
            .unwrap(classOf[CalciteConnection])
            .getRootSchema
            .add("scalcite", ScalciteCirceTable(json))
          SQL(query).map(toMap)
            .list()
            .apply()
            .head
        }).map(_.asJson)
          .mapError {
            case e: SQLException => Unknown(e
                .getCause
                .getMessage
                .dropWhile(c => c != ':')
                .drop(2))
          }
        //.iterator()
        //.withDBSessionForceAdjuster(session => {
      }

  private def toMap: WrappedResultSet => Map[String, Json] =
    rs => {
      (1 to rs.metaData.getColumnCount).foldLeft(Map[String, Json]()) { (result, i) =>
        val label = rs.metaData.getColumnLabel(i)
        Option(rs.any(label))
          .map { value =>
            val r: Json = value match {
              case s: String => Json.fromString(s)
              case b: Boolean => Json.fromBoolean(b)
              case i: Integer => Json.fromInt(i)
              case l: Long => Json.fromLong(l)
              case f: Float => Json.fromFloatOrNull(f)
              case i: BigInt => Json.fromBigInt(i)
              case d: BigDecimal => Json.fromBigDecimal(d)
            }

            result + (label -> r)

          }
          .getOrElse(result)
      }
    }
}

object RelationalCalciteRepository {
  def apply(databaseName: String)(implicit executionContext: ExecutionContext): RelationalCalciteRepository =
    new RelationalCalciteRepository(databaseName)(executionContext)
}
