package io.circe.scalcite.example

import java.util

import io.circe._
import io.github.mvillafuertem.scalcite.ScalciteType
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.{Statistic, Statistics}
import org.apache.calcite.util.Pair

final class ScalciteTypeImpl(json: Json) extends ScalciteType {

  private val keys: Iterable[String] = json.asObject.get.keys
  private val values: Iterable[Json] = json.asObject.get.values

  override def rowtype(typeFactory: RelDataTypeFactory): RelDataType = {
    val types: util.List[RelDataType] = new util.ArrayList[RelDataType]
    val names: util.List[String] = new util.ArrayList[String]

    keys.foreach(names.add)
    values.map {
        case Json.JNull => types.add(typeFactory.createJavaType(classOf[java.lang.String]))
        case Json.JBoolean(value) => types.add(typeFactory.createJavaType(classOf[java.lang.Boolean]))
        case Json.JNumber(value) => types.add(typeFactory.createJavaType(classOf[Number]))
        case Json.JString(value) => types.add(typeFactory.createJavaType(classOf[java.lang.String]))
        case Json.JArray(value) => throw new RuntimeException("type not supported")
        case Json.JObject(value) => throw new RuntimeException("type not supported")
      }

    println(types)
    println(names)

    typeFactory.createStructType(Pair.zip(names, types))

  }

  override def statistic: Statistic = Statistics.UNKNOWN

  override def enumerator: Array[Any] = {
    values.toArray.map {
      case Json.JNull => "null"
    case Json.JBoolean(value) => value
    case Json.JNumber(value) => value match {
      case number: BiggerDecimalJsonNumber => throw new RuntimeException("type not supported")
      case JsonBigDecimal(value) => value
      case JsonLong(value) => value
      case JsonDouble(value) => value
      case JsonFloat(value) => value
    }
    case Json.JString(value) => value
      case Json.JArray(value) => throw new RuntimeException("type not supported")
      case Json.JObject(value) => throw new RuntimeException("type not supported")
    }
  }
}

object ScalciteTypeImpl {
  def apply(json: Json): ScalciteTypeImpl = new ScalciteTypeImpl(json)
}
