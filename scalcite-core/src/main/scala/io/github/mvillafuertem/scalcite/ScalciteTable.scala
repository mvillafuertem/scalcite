package io.github.mvillafuertem.scalcite

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.{AbstractEnumerable, Enumerable, Enumerator}
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.schema.{ScannableTable, Statistic}

object ScalciteTable {
  def apply(scalciteType: ScalciteType): ScalciteTable = new ScalciteTable(scalciteType)
}

class ScalciteTable(scalciteType: ScalciteType) extends AbstractTable with ScannableTable {

  override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = scalciteType.rowtype(typeFactory)

  override def getStatistic: Statistic = scalciteType.statistic

  override def scan(root: DataContext): Enumerable[Array[AnyRef]] =
    new AbstractEnumerable[Array[AnyRef]]() {
      override def enumerator: Enumerator[Array[AnyRef]] = new ScalciteEnumerator(scalciteType.enumerator).asInstanceOf[Enumerator[Array[AnyRef]]]
    }
}
