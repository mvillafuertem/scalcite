package io.github.mvillafuertem.scalcite

import java.sql.{Connection, ResultSet, SQLException}

import scala.collection.mutable

object JsonQuery {
  def apply(connection: Connection): JsonQuery = new JsonQuery(connection)
}

final class JsonQuery(connection: Connection) {

  def sql(statements: List[String]): mutable.Map[String, Any] = {
    val map: mutable.Map[String, Any] = new mutable.LinkedHashMap[String, Any]
    statements.foreach(a => map.put("", sql(a)))
    map
  }

  def sql(sql: String): mutable.Map[String, Any] = {
    val statement = connection.createStatement
    val resultSet = statement.executeQuery(sql)
    resultSetToMap(resultSet)
  }

  @throws[SQLException]
  private def resultSetToMap(resultSet: ResultSet): mutable.Map[String, Any] = {
    val map: mutable.Map[String, Any] = new mutable.LinkedHashMap[String, Any]
    if (resultSet.next()) {
      for (i <- 1 to resultSet.getMetaData.getColumnCount) {
        map.put(resultSet.getMetaData.getColumnLabel(i), resultSet.getObject(i))
      }
    }
    resultSet.close()
    map
  }

}
