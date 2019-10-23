package io.github.mvillafuertem.blower


final class JsonBlower {

  def toJsonString(jsonString: String): String = {
    JsonParser parse blowUp(jsonString)
  }

  def toMap(jsonString: String): Map[String, Any] = {
    blowUp(jsonString)
  }

  private def blowUp(jsonString: String): Map[String, Any] = {
    val parsedJson = JsonParser parse jsonString
    MapBlower.apply(parsedJson).blowUp
  }

}
