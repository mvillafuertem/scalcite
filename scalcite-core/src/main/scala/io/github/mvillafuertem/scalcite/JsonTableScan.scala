package io.github.mvillafuertem.scalcite

import java.util

import org.apache.calcite.adapter.enumerable.{EnumerableConvention, EnumerableRel, EnumerableRelImplementor, PhysTypeImpl}
import org.apache.calcite.linq4j.tree.{Blocks, Expressions, Primitive}
import org.apache.calcite.plan._
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory, RelDataTypeField}
import org.apache.calcite.rel.core.TableScan
import org.apache.calcite.rel.metadata.RelMetadataQuery
import org.apache.calcite.rel.{RelNode, RelWriter}

final class JsonTableScan(cluster: RelOptCluster,
                          table: RelOptTable,
                          val jsonTable: JsonTranslatableTable,
                          fields: Array[Int])
  extends TableScan(cluster, cluster.traitSetOf(EnumerableConvention.INSTANCE), table)
    with EnumerableRel {

  override def copy(traitSet: RelTraitSet, inputs: util.List[RelNode]): RelNode = {
    if (!inputs.isEmpty) throw new AssertionError
    new JsonTableScan(getCluster, table, jsonTable, fields)
  }

  override def explainTerms(pw: RelWriter): RelWriter =
    super.explainTerms(pw).item("fields", Primitive.asList(fields))


  override def deriveRowType(): RelDataType = {
    val fieldList: util.List[RelDataTypeField] = table.getRowType.getFieldList
    val builder: RelDataTypeFactory.Builder = getCluster.getTypeFactory.builder
    for (field <- fields) {
      builder.add(fieldList.get(field))
    }
     builder.build
  }

  override def register(planner: RelOptPlanner): Unit = planner.addRule(JsonProjectTableScanRule.INSTANCE)

  override def computeSelfCost(planner: RelOptPlanner, mq: RelMetadataQuery): RelOptCost =
    super.computeSelfCost(planner, mq).multiplyBy((fields.length.toDouble + 2D) / (table.getRowType.getFieldCount.toDouble + 2D))

  override def implement(implementor: EnumerableRelImplementor, pref: EnumerableRel.Prefer): EnumerableRel.Result = {
    val physType = PhysTypeImpl.of(implementor.getTypeFactory, getRowType, pref.preferArray)

    if (table.isInstanceOf[JsonTable]) return implementor.result(physType, Blocks.toBlock(Expressions.call(table.getExpression(classOf[JsonTable]), "enumerable")))

    implementor.result(physType, Blocks.toBlock(Expressions.call(table.getExpression(classOf[JsonTranslatableTable]), "project", implementor.getRootExpression, Expressions.constant(fields))))

  }

}
