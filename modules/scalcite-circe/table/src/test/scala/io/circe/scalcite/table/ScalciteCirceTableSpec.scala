package io.circe.scalcite.table

import java.sql.{Connection, DriverManager, ResultSet, SQLException}
import java.util.Properties

import io.circe.Json
import org.apache.calcite.jdbc.CalciteConnection
import org.apache.calcite.schema.Table
import org.apache.calcite.schema.impl.AbstractSchema
import org.scalatest.flatspec.AnyFlatSpecLike

class ScalciteCirceTableSpec extends AnyFlatSpecLike {


  behavior of "BeatlesTableSpec"

  it should "print" in {

    val json: Json = Json.obj(
      ("id", Json.fromString("c730433b-082c-4984-9d66-855c243266f0")),
      ("name", Json.fromString("Foo")),
      ("counts", Json.arr(Json.fromInt(1), Json.fromInt(2), Json.fromInt(3))),
      ("values.bar", Json.fromBoolean(true)),
      ("values.baz", Json.fromDoubleOrNull(100.001)),
      ("values.qux", Json.fromString("b"))
    )
    val circeTable = ScalciteCirceTable(json)
    val connection = createConnection("s", "scalcite", circeTable)


    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("select * from \"s\".\"scalcite\"")


    val str = rowToString(resultSet)
    println(str)



    resultSet.close()
    statement.close()
    connection.close()



  }


  @throws[SQLException]
  def createConnection(schemaName: String,
                       tableName: String,
                       table: Table): Connection = {

    Class.forName("org.apache.calcite.jdbc.Driver")
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


        println(resultSet.getMetaData.getColumnLabel(i))
        println(resultSet.getString(i))
        builder
          .append(resultSet.getMetaData.getColumnLabel(i))
          .append("=")
          .append(resultSet.getString(i))
          .append("; ")
      }
    }
    builder
  }

}
