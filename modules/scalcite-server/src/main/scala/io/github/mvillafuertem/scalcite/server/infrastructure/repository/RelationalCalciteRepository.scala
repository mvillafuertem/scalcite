package io.github.mvillafuertem.scalcite.server.infrastructure.repository

import java.sql.SQLException
import io.circe.Json
import io.circe.scalcite.table.ScalciteCirceTable
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.server.domain.error.ScalciteError.Unknown
import io.github.mvillafuertem.scalcite.server.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.server.domain.repository.CalciteRepository
import org.apache.calcite.jdbc.CalciteConnection
import scalikejdbc.{WrappedResultSet, _}
import zio.stream.ZStream
import zio.{Has, Task, ULayer, ZLayer, stream}

/**
 * @author Miguel Villafuerte
 */
private final class RelationalCalciteRepository(databaseName: String) extends CalciteRepository {

  override def queryForMap(map: collection.Map[String, Any], query: String): stream.Stream[ScalciteError, collection.Map[String, Any]] =
    ZStream.fromEffect {
      Task
        .effect(NamedDB(Symbol(databaseName)) autoCommit { implicit session =>
          session.connection
            .unwrap(classOf[CalciteConnection])
            .getRootSchema
            .add("scalcite", ScalciteMapTable(map))
          SQL(query)
            .map(_.toMap())
            .list()
            .apply()
            .head
        })
        .mapError {
          case e: SQLException =>
            Unknown(
              e.getCause.getMessage
                .dropWhile(c => c != ':')
                .drop(2)
            )
        }
    }

  override def queryForJson(json: Json, query: String): stream.Stream[ScalciteError, Json] =
    ZStream.fromEffect {
      Task
        .effect(NamedDB(Symbol(databaseName)) autoCommit { implicit session =>
          session.connection
            .unwrap(classOf[CalciteConnection])
            .getRootSchema
            .add("scalcite", ScalciteCirceTable(json))
          SQL(query)
            .map(toMap)
            .list()
            .apply()
            .head
        })
        .map(_.asJson)
        .mapError {
          case e: SQLException =>
            Unknown(
              e.getCause.getMessage
                .dropWhile(c => c != ':')
                .drop(2)
            )
        }
    }

  private def toMap: WrappedResultSet => Map[String, Json] =
    rs => {
      (1 to rs.metaData.getColumnCount).foldLeft(Map[String, Json]()) { (result, i) =>
        val label = rs.metaData.getColumnLabel(i)
        Option(rs.any(label)).map { value =>
          val r: Json = value match {
            case s: String     => Json.fromString(s)
            case b: Boolean    => Json.fromBoolean(b)
            case i: Integer    => Json.fromInt(i)
            case l: Long       => Json.fromLong(l)
            case f: Float      => Json.fromFloatOrNull(f)
            case i: BigInt     => Json.fromBigInt(i)
            case d: BigDecimal => Json.fromBigDecimal(d)
          }

          result + (label -> r)

        }.getOrElse(result)
      }
    }
}
object RelationalCalciteRepository {

  def apply(databaseName: String): CalciteRepository =
    new RelationalCalciteRepository(databaseName)

  type ZCalciteRepository = Has[CalciteRepository]

  def queryForMap(map: collection.Map[String, Any], query: String): stream.ZStream[ZCalciteRepository, ScalciteError, collection.Map[String, Any]] =
    stream.ZStream.accessStream(_.get.queryForMap(map, query))

  def queryForJson(json: Json, query: String): stream.ZStream[ZCalciteRepository, ScalciteError, Json] =
    stream.ZStream.accessStream(_.get.queryForJson(json, query))

  val live: ZLayer[Has[String], Nothing, ZCalciteRepository] =
    ZLayer.fromService[String, CalciteRepository](RelationalCalciteRepository(_))

  def make(databaseName: String): ULayer[ZCalciteRepository] =
    ZLayer.succeed(databaseName) >>> live

}
