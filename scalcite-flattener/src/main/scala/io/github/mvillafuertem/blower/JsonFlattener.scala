package io.github.mvillafuertem.blower


final class JsonFlattener {

  def toJsonString(jsonString: String): String = {
    JsonParser parse flatten(jsonString)
  }

  def toMap(jsonString: String): Map[String, Any] = {
    flatten(jsonString)
  }

  private def flatten(jsonString: String): Map[String, Any] = {
    val parsedJson = JsonParser parse jsonString
    MapFlattener.apply(parsedJson).flatten
  }
}
