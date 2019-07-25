package io.github.mvillafuertem.scalcite.beatles

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.{AbstractEnumerable, Enumerable, Enumerator}
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.ScannableTable
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.sql.`type`.SqlTypeName

/** Table that returns two columns via the ScannableTable interface. */
class BeatlesTable extends AbstractTable with ScannableTable {

  override def getRowType(typeFactory: RelDataTypeFactory): RelDataType =
    typeFactory.builder
      .add("i", SqlTypeName.INTEGER)
      .add("j", SqlTypeName.VARCHAR).build

  override def scan(root: DataContext): Enumerable[Array[AnyRef]] = new AbstractEnumerable[Array[AnyRef]]() {
    override def enumerator: Enumerator[Array[AnyRef]] =
      new BeatlesEnumerator(new StringBuilder, null, null)
  }

}