package io.github.mvillafuertem.scalcite

import java.util

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.{AbstractEnumerable, Enumerable, Enumerator}
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.schema.{ScannableTable, Statistic, Statistics}
import org.apache.calcite.util.Pair

import scala.collection.JavaConverters._

object JsonTable {
  def apply(map: Map[String, Any]): JsonTable = new JsonTable(map)
}

final class JsonTable(map: Map[String, Any]) extends AbstractTable with ScannableTable {

  private val javaMap = map.asJava

  override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {

    val types: util.List[RelDataType] = new util.ArrayList[RelDataType]
    val names: util.List[String] = new util.ArrayList[String]
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

  override def scan(root: DataContext): Enumerable[Array[AnyRef]] =
    new AbstractEnumerable[Array[AnyRef]]() {
      override def enumerator: Enumerator[Array[AnyRef]] = new JsonEnumerator(javaMap.values.toArray)
    }
}
