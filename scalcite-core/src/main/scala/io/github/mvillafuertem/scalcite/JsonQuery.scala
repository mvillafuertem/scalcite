package io.github.mvillafuertem.scalcite

import java.sql.{Connection, ResultSet}

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

object JsonQuery {
  def apply(connection: Connection): JsonQuery = new JsonQuery(connection)
}

final class JsonQuery(connection: Connection) {

  def sql(statements: List[String]): Try[Map[String, Any]] = {
    val map: mutable.Map[String, Any] = mutable.LinkedHashMap[String, Any]()
    statements.foreach(a => {
      sql(a) match {
        case Success(value) => map ++= value
        case Failure(exception) => exception
      }
    })
    Try(map.toMap)
  }

  def sql(sql: String): Try[Map[String, Any]] = {
    val statement = connection.createStatement
    Try(statement.executeQuery(sql)) match {
      case Success(value) =>
        Try(resultSetToMap(value))
      case Failure(exception) => Failure(exception)
    }
  }

  private def resultSetToMap(resultSet: ResultSet): Map[String, Any] = {
    val map: mutable.Map[String, Any] = mutable.LinkedHashMap[String, Any]()
    if (resultSet.next()) {
      for (i <- 1 to resultSet.getMetaData.getColumnCount) {
        map.put(resultSet.getMetaData.getColumnLabel(i), resultSet.getObject(i))
      }
    }
    resultSet.close()
    map.toMap
  }

}
