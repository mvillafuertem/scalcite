package io.github.mvillafuertem.scalcite

import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.Statistic


trait ScalciteType {

  def rowtype(typeFactory: RelDataTypeFactory): RelDataType

  def statistic: Statistic

  def enumerator: Array[Any]

}

