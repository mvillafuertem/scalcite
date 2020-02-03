package io.github.mvillafuertem.scalcite

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.{AbstractEnumerable, Enumerable, Enumerator}
import org.apache.calcite.schema.ScannableTable



object MapScannableTable {
  def apply(map: Map[String, Any]): MapScannableTable = new MapScannableTable(map)
}

final class MapScannableTable(map: Map[String, Any])
  extends MapTable(map)
  with ScannableTable {

  override def scan(root: DataContext): Enumerable[Array[AnyRef]] =
    new AbstractEnumerable[Array[AnyRef]]() {
      override def enumerator: Enumerator[Array[AnyRef]] = new ScalciteEnumerator(map.values.toArray).asInstanceOf[Enumerator[Array[AnyRef]]]
    }

}
