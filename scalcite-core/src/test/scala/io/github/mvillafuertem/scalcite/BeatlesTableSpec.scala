package io.github.mvillafuertem.scalcite

import java.sql.{Connection, DriverManager, ResultSet, SQLException}
import java.util.Properties

import io.github.mvillafuertem.scalcite.beatles.BeatlesTable
import org.apache.calcite.jdbc.CalciteConnection
import org.apache.calcite.schema.Table
import org.apache.calcite.schema.impl.AbstractSchema
import org.scalatest.{FlatSpec, Matchers}


final class BeatlesTableSpec extends FlatSpec with Matchers {


  behavior of "BeatlesTableSpec"

  it should "print" in {

    val connection = createConnection("s", "beatles", new BeatlesTable)


    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("select * from \"s\".\"beatles\"")


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
