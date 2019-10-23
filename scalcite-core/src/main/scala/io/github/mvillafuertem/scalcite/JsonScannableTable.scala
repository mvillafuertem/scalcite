package io.github.mvillafuertem.scalcite

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.{AbstractEnumerable, Enumerable, Enumerator}
import org.apache.calcite.schema.ScannableTable



object JsonScannableTable {
  def apply(map: Map[String, Any]): JsonScannableTable = new JsonScannableTable(map)
}

final class JsonScannableTable(map: Map[String, Any])
  extends JsonTable(map)
  with ScannableTable {

  override def scan(root: DataContext): Enumerable[Array[AnyRef]] =
    new AbstractEnumerable[Array[AnyRef]]() {
      override def enumerator: Enumerator[Array[AnyRef]] = new JsonEnumerator(map.values.toArray).asInstanceOf[Enumerator[Array[AnyRef]]]
    }

}
