package io.github.mvillafuertem.scalcite

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.{AbstractEnumerable, Enumerable, Enumerator}
import org.apache.calcite.schema.ScannableTable
import org.apache.calcite.schema.impl.AbstractTable

trait ScalciteTable extends AbstractTable with ScannableTable {

  def array: Array[Any]

  override def scan(root: DataContext): Enumerable[Array[AnyRef]] =
    new AbstractEnumerable[Array[AnyRef]]() {
      override def enumerator: Enumerator[Array[AnyRef]] = new ScalciteEnumerator(array).asInstanceOf[Enumerator[Array[AnyRef]]]
    }
}
