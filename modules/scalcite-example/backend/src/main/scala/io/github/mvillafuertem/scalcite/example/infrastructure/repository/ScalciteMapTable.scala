package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import java.util

import io.github.mvillafuertem.scalcite.ScalciteTable
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.{Statistic, Statistics}
import org.apache.calcite.util.Pair

final class ScalciteMapTable(map: collection.Map[String, Any]) extends ScalciteTable {

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

  override def array: Array[Any] = map.values.toArray
}

object ScalciteMapTable {
  def apply(map: collection.Map[String, Any]): ScalciteMapTable = new ScalciteMapTable(map)
}
