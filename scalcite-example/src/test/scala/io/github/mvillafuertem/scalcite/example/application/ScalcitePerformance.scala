package io.github.mvillafuertem.scalcite.example.application

import java.sql.DriverManager
import java.util.Properties

import io.github.mvillafuertem.mapflablup.JsonFlatten
import io.github.mvillafuertem.scalcite.JsonTranslatableTable
import org.apache.calcite.config.{CalciteConnectionProperty, Lex}
import org.apache.calcite.jdbc.CalciteConnection
import org.scalatest.FlatSpec

final class ScalcitePerformance extends FlatSpec {


  it should "performance" in {

    for (n <- 1 to 1) {

      Class.forName("org.apache.calcite.jdbc.Driver")
      val properties = new Properties
      properties.put(CalciteConnectionProperty.LEX.camelName, Lex.MYSQL.name)
      val connection = DriverManager.getConnection("jdbc:calcite:caseSensitive=false&defaultSchema=json", properties)
      val optiqConnection = connection.unwrap(classOf[CalciteConnection])
      val rootSchema = optiqConnection.getRootSchema
      rootSchema.setCacheEnabled(true)

      val asset = """{"_id":"5c5f1f313fcc6e3084fbe65e","index":0,"guid":"f3b5960b-f3e1-4556-9a5d-f552afe204e7","isActive":true,"balance":"$2,809.92","picture":"http://placehold.it/32x32","age":28,"eyeColor":"blue","personalinfo":{"name":"Elliott Kaufman","gender":"male","phone":"+1 (858) 421-2925","email":"elliottkaufman@spacewax.com","address":"952 Cropsey Avenue, Tyro, Guam, 1787","company":{"name":"SPACEWAX"}},"about":"Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.\r\n","registered":"2015-03-28T06:35:33 -01:00","location":{"latitude":78.370719,"longitude":-137.117139},"greeting":"Hello, Elliott Kaufman! You have 5 unread messages.","favoriteFruit":"strawberry"}"""
      val flatten = new JsonFlatten toMap asset

      rootSchema.add("person", JsonTranslatableTable(flatten))


      val statement = connection.createStatement
      statement.execute("SELECT `personalinfo.name` FROM person")




      statement.close()
      connection.close()
    }



  }


}
