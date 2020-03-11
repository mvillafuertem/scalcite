package io.circe.scalcite.table

import java.util

import io.circe._
import io.github.mvillafuertem.scalcite.ScalciteTable
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.{Statistic, Statistics}
import org.apache.calcite.util.Pair

final class ScalciteCirceTable(json: Json) extends ScalciteTable {

  private val keys: Iterable[String] = json.asObject.get.keys
  private val values: Iterable[Json] = json.asObject.get.values

  override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {
    val types: util.List[RelDataType] = new util.ArrayList[RelDataType]
    val names: util.List[String] = new util.ArrayList[String]

    keys.foreach(names.add)
    values.foreach {
      case Json.JNull => types.add(typeFactory.createJavaType(classOf[java.lang.String]))
      case Json.JBoolean(_) => types.add(typeFactory.createJavaType(classOf[java.lang.Boolean]))
      case Json.JNumber(value) => value match {
        case _: BiggerDecimalJsonNumber => throw new RuntimeException("type not supported")
        case JsonBigDecimal(_) => types.add(typeFactory.createJavaType(classOf[java.math.BigDecimal]))
        case JsonLong(_) => types.add(typeFactory.createJavaType(classOf[java.lang.Long]))
        case JsonDouble(_) => types.add(typeFactory.createJavaType(classOf[java.lang.Double]))
        case JsonFloat(_) => types.add(typeFactory.createJavaType(classOf[java.lang.Float]))
      }
      case Json.JString(_) => types.add(typeFactory.createJavaType(classOf[java.lang.String]))
      case Json.JArray(_) => throw new RuntimeException("type not supported")
      case Json.JObject(_) => throw new RuntimeException("type not supported")
    }

    typeFactory.createStructType(Pair.zip(names, types))

  }

  override def getStatistic: Statistic = Statistics.UNKNOWN

  override def array: Array[Any] = {
    values.toArray.map {
      case Json.JNull => "null"
      case Json.JBoolean(value) => value
      case Json.JNumber(value) => value match {
        case _: BiggerDecimalJsonNumber => throw new RuntimeException("type not supported")
        case JsonBigDecimal(value) => value
        case JsonLong(value) => value
        case JsonDouble(value) => value
        case JsonFloat(value) => value
      }
      case Json.JString(value) => value
      case Json.JArray(_) => throw new RuntimeException("type not supported")
      case Json.JObject(_) => throw new RuntimeException("type not supported")
    }
  }
}

object ScalciteCirceTable {
  def apply(json: Json): ScalciteCirceTable = new ScalciteCirceTable(json)
}
