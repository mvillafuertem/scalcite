package io.github.mvillafuertem.scalcite

import java.util

import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.schema.{Statistic, Statistics}
import org.apache.calcite.util.Pair

import scala.collection.JavaConverters._

object JsonTable {
  def apply(map: Map[String, Any]): JsonTable = new JsonTable(map)
}

class JsonTable(map: Map[String, Any]) extends AbstractTable {

  private val javaMap = map.asJava

  var types: util.List[RelDataType] = _
  var names: util.List[String] = _

  override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {

    types = new util.ArrayList[RelDataType]
    names = new util.ArrayList[String]

    val var5 = javaMap.keySet.iterator
    while (var5.hasNext) {
      val string: String = var5.next
      val dataType: RelDataType = typeFactory.createJavaType(javaMap.get(string).getClass)
      names.add(string)
      types.add(dataType)
    }
    typeFactory.createStructType(Pair.zip(names, types))
  }

  override def getStatistic: Statistic = Statistics.UNKNOWN

}
