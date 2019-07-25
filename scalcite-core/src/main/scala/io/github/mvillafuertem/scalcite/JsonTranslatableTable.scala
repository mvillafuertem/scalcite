package io.github.mvillafuertem.scalcite

import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.tree.Expression
import org.apache.calcite.linq4j.{AbstractEnumerable, Enumerator, QueryProvider}
import org.apache.calcite.plan.RelOptTable
import org.apache.calcite.rel.RelNode
import org.apache.calcite.schema.{QueryableTable, SchemaPlus, Schemas, TranslatableTable}

import scala.collection.JavaConverters._

final class JsonTranslatableTable(map: Map[String, Any])
  extends JsonTable(map)
    with QueryableTable
    with TranslatableTable {


  def project(root: DataContext, fields: Array[Int]) = {
    val cancelFlag: AtomicBoolean = DataContext.Variable.CANCEL_FLAG.get(root)
    new AbstractEnumerable[Array[AnyRef]]() {
      override def enumerator: Enumerator[Array[AnyRef]] = new JsonEnumerator(map.asJava.values.toArray)
    }
  }


  override def asQueryable[T](queryProvider: QueryProvider, schema: SchemaPlus, tableName: String) =
    throw new UnsupportedOperationException

  override def getElementType: Type = classOf[AnyRef]


  override def getExpression(schema: SchemaPlus, tableName: String, clazz: Class[_]): Expression =
    Schemas.tableExpression(schema, getElementType, tableName, clazz)


  override def toRel(context: RelOptTable.ToRelContext, relOptTable: RelOptTable): RelNode = {

    val fieldCount = relOptTable.getRowType.getFieldCount
    val fields = JsonEnumerator.identityList(fieldCount)
    new JsonTableScan(context.getCluster, relOptTable, this, fields)
  }

}

object JsonTranslatableTable {

  def apply(map: Map[String, Any]): JsonTranslatableTable = new JsonTranslatableTable(map)

}
