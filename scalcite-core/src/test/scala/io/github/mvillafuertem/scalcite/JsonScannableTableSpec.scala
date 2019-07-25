package io.github.mvillafuertem.scalcite

import java.sql.{Connection, DriverManager, ResultSet, SQLException}
import java.util.Properties

import org.apache.calcite.jdbc.CalciteConnection
import org.apache.calcite.schema.Table
import org.apache.calcite.schema.impl.AbstractSchema
import org.scalatest.{FlatSpec, Matchers}

final class JsonScannableTableSpec extends FlatSpec with Matchers {



  it should "return fields" in {

    // G I V E N
    val map: Map[String, Any] = Map("name" -> "Pepe", "age" -> "60")
    val connection = createConnection("s", "beatles", new JsonScannableTable(map))

    // W H E N
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("select * from \"s\".\"beatles\"")

    // T H E N
    val str = rowToString(resultSet)
    str shouldBe "name=Pepe; age=60; "

    resultSet.close()
    statement.close()
    connection.close()

  }

  it should "return plan" in {

    // G I V E N
    val map: Map[String, Any] = Map("name" -> "Pepe", "age" -> "60")
    val connection = createConnection("s", "beatles", new JsonScannableTable(map))

    // W H E N
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("explain plan for select * from \"s\".\"beatles\"")

    // T H E N
    val str = rowToString(resultSet)
    str shouldBe "PLAN=EnumerableInterpreter\n  BindableTableScan(table=[[s, beatles]])\n; "

    resultSet.close()
    statement.close()
    connection.close()

  }

  @throws[SQLException]
  def createConnection(schemaName: String,
                       tableName: String,
                       table: Table): Connection = {
    val info = new Properties
    val connection = DriverManager
      .getConnection("jdbc:calcite:", info)
      .unwrap(classOf[CalciteConnection])
    val rootSchema = connection.getRootSchema
    val schema = rootSchema.add(schemaName, new AbstractSchema)
    schema.add(tableName, table)
    connection.setSchema(schemaName)
    connection

  }

  @throws[SQLException]
  private def rowToString(resultSet: ResultSet) = {

    val builder = new StringBuilder()
    while (resultSet.next()) {
      for (i <- 1 to resultSet.getMetaData.getColumnCount) {
        builder
          .append(resultSet.getMetaData.getColumnName(i))
          .append("=")
          .append(resultSet.getString(i))
          .append("; ")
      }
    }
    builder.toString()
  }

}
