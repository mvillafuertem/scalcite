package io.github.mvillafuertem.scalcite

import java.util

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.{AbstractEnumerable, Enumerable, Enumerator}
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.schema.{ScannableTable, Statistic, Statistics}
import org.apache.calcite.util.Pair

object MapTable {
  def apply(map: collection.Map[String, Any]): MapTable = new MapTable(map)
}

class MapTable(map: collection.Map[String, Any]) extends AbstractTable with ScannableTable {

  override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {

    val types: util.List[RelDataType] = new util.ArrayList[RelDataType]
    val names: util.List[String] = new util.ArrayList[String]

    map.foreach(value => {
      // C O L U M N
      names.add(value._1)
      // F I E L D S
      val dataType: RelDataType = typeFactory.createJavaType(value._2.getClass)
      types.add(dataType)
    })

    typeFactory.createStructType(Pair.zip(names, types))
  }

  override def getStatistic: Statistic = Statistics.UNKNOWN

  override def scan(root: DataContext): Enumerable[Array[AnyRef]] =
    new AbstractEnumerable[Array[AnyRef]]() {
      override def enumerator: Enumerator[Array[AnyRef]] = new ScalciteEnumerator(map.values.toArray).asInstanceOf[Enumerator[Array[AnyRef]]]
    }
}
