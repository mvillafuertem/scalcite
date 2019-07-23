package io.github.mvillafuertem.scalcite

import java.util

import org.apache.calcite.plan.RelOptRule._
import org.apache.calcite.plan.{RelOptRule, RelOptRuleCall}
import org.apache.calcite.rel.core.RelFactories
import org.apache.calcite.rel.logical.LogicalProject
import org.apache.calcite.rex.{RexInputRef, RexNode}
import org.apache.calcite.tools.RelBuilderFactory

final class JsonProjectTableScanRule(relBuilderFactory: RelBuilderFactory)
  extends RelOptRule(
    operand(classOf[LogicalProject], operand(classOf[JsonTableScan], none)),
    relBuilderFactory,
    "JsonProjectTableScanRule") {


  override def onMatch(call: RelOptRuleCall): Unit = {
    val project: LogicalProject = call.rel(0)
    val scan: JsonTableScan  = call.rel(1)
    val fields = getProjectFields(project.getProjects)
    if (fields == null) { // Project contains expressions more complex than just field references.
      return
    }
    call.transformTo(new JsonTableScan(scan.getCluster, scan.getTable, scan.jsonTable, fields))
  }

  def getProjectFields(exps: util.List[RexNode]): Array[Int] = {

    val fields = new Array[Int](exps.size)
    var i = 0
    while ( {
      i < exps.size
    }) {
      val exp = exps.get(i)
      if (exp.isInstanceOf[RexInputRef]) fields(i) = exp.asInstanceOf[RexInputRef].getIndex
      else return null // not a simple projection

      {
        i += 1; i - 1
      }
    }
    fields

  }

}

object JsonProjectTableScanRule {

  val INSTANCE = new JsonProjectTableScanRule(RelFactories.LOGICAL_BUILDER)

}
