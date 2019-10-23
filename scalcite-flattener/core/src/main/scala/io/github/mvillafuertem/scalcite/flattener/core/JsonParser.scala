package io.github.mvillafuertem.scalcite.flattener.core

import org.json4s.DefaultFormats
import org.json4s.jackson.{JsonMethods, Serialization}

/**
 * @author Miguel Villafuerte
 */
object JsonParser {

  private implicit val formats: DefaultFormats = org.json4s.DefaultFormats

  def parse(jsonString: String): Map[String, Any] = {
    JsonMethods.parse(jsonString).extract[Map[String, Any]]
  }

  def parse(map: Map[String, Any]): String = {
    Serialization.write(map)
  }
}
