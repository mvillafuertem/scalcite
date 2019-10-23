package io.github.mvillafuertem.blower

import org.json4s._
import org.json4s.jackson.{JsonMethods, Serialization}

object JsonParser {

  private implicit val formats: DefaultFormats = org.json4s.DefaultFormats

  def parse(jsonString: String): Map[String, Any] = {
    JsonMethods.parse(jsonString).extract[Map[String, Any]]
  }

  def parse(map: Map[String, Any]): String = {
    Serialization.write(map)
  }
}
