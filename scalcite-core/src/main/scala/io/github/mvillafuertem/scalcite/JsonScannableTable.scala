package io.github.mvillafuertem.scalcite

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.{AbstractEnumerable, Enumerable, Enumerator}
import org.apache.calcite.schema.ScannableTable

import scala.collection.JavaConverters._



object JsonScannableTable {
  def apply(map: Map[String, Any]): JsonScannableTable = new JsonScannableTable(map)
}

final class JsonScannableTable(map: Map[String, Any])
  extends JsonTable(map)
  with ScannableTable {

  private val javaMap = map.asJava

  override def scan(root: DataContext): Enumerable[Array[AnyRef]] =
    new AbstractEnumerable[Array[AnyRef]]() {
      override def enumerator: Enumerator[Array[AnyRef]] = new JsonEnumerator(javaMap.values.toArray)
    }

}
