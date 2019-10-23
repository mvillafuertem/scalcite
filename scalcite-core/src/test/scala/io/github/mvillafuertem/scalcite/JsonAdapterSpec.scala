package io.github.mvillafuertem.scalcite

import java.util

import io.github.mvillafuertem.blower.JsonFlattener
import org.apache.calcite.adapter.enumerable.EnumerableTableScan
import org.apache.calcite.plan._
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.SchemaPlus
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.sql.{SqlExplainFormat, SqlExplainLevel}
import org.apache.calcite.tools.Frameworks
import org.apache.calcite.util.{Pair, Util}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._


final class JsonAdapterSpec extends FlatSpec with Matchers {

  val table: AbstractTable = new AbstractTable() {

    override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {

      val asset = """{"_id":"5c5f1f313fcc6e3084fbe65e","index":0,"guid":"f3b5960b-f3e1-4556-9a5d-f552afe204e7","isActive":true,"balance":"$2,809.92","picture":"http://placehold.it/32x32","age":28,"eyeColor":"blue","personalinfo":{"name":"Elliott Kaufman","gender":"male","phone":"+1 (858) 421-2925","email":"elliottkaufman@spacewax.com","address":"952 Cropsey Avenue, Tyro, Guam, 1787","company":{"name":"SPACEWAX"}},"about":"Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.\r\n","registered":"2015-03-28T06:35:33 -01:00","location":{"latitude":78.370719,"longitude":-137.117139},"greeting":"Hello, Elliott Kaufman! You have 5 unread messages.","favoriteFruit":"strawberry"}"""
      val flatten = new JsonFlattener toMap asset
      val javaMap = flatten.asJava

      val types: util.List[RelDataType] = new util.ArrayList[RelDataType]
      val names: util.List[String] = new util.ArrayList[String]
      val var5 = javaMap.keySet.iterator
      while (var5.hasNext) {
        val string: String = var5.next
        val dataType: RelDataType = typeFactory.createJavaType(javaMap.get(string).getClass)
        names.add(string)
        types.add(dataType)
      }
      typeFactory.createStructType(Pair.zip(names, types))

    }

  }

  it should "" in {

    val x = Frameworks.withPlanner((cluster: RelOptCluster, relOptSchema: RelOptSchema,
                                    rootSchema: SchemaPlus) => {

      def foo(cluster: RelOptCluster,
              relOptSchema: RelOptSchema,
              rootSchema: SchemaPlus) = {
        val typeFactory = cluster.getTypeFactory


        // "SELECT * FROM myTable"
        val relOptTable: RelOptAbstractTable = new RelOptAbstractTable(relOptSchema, "myTable", table.getRowType(typeFactory)) {}
        val tableRel: EnumerableTableScan = EnumerableTableScan.create(cluster, relOptTable)
        // "WHERE i > 1"
//        val rexBuilder: RexBuilder = cluster.getRexBuilder
//        val condition: RexNode = rexBuilder.makeCall(
//          SqlStdOperatorTable.GREATER_THAN,
//          rexBuilder.makeFieldAccess(rexBuilder.makeRangeReference(tableRel),
//            "guid", true),
//          rexBuilder.makeExactLiteral(BigDecimal.ONE))
//        val filter: LogicalFilter = LogicalFilter.create(tableRel, condition)
//        // Specify that the result should be in Enumerable convention.
//        val rootRel: LogicalFilter = filter
//        val planner: RelOptPlanner = cluster.getPlanner
//        val desiredTraits: RelTraitSet = cluster.traitSet.replace(EnumerableConvention.INSTANCE)
//        val rootRel2: RelNode = planner.changeTraits(rootRel, desiredTraits)
//        planner.setRoot(rootRel2)
//        // Now, plan.
//        planner.findBestExp
        tableRel
      }

      foo(cluster, relOptSchema, rootSchema)
    })

    val s = RelOptUtil.dumpPlan("", x, SqlExplainFormat.TEXT, SqlExplainLevel.DIGEST_ATTRIBUTES)


    info(Util.toLinux(s))

    //Util.toLinux(s) shouldBe ("EnumerableFilter(condition=[>($1, 1)])\n" + "  EnumerableTableScan(table=[[myTable]])\n")

  }




  it should "return a EnumerableTableScan" in {

    // G I V E N
    val selectAllFromMyTable = Frameworks.withPlanner((cluster: RelOptCluster, relOptSchema: RelOptSchema, _: SchemaPlus) => {
      val typeFactory = cluster.getTypeFactory
      // "SELECT * FROM myTable"
      val relOptTable: RelOptAbstractTable = new RelOptAbstractTable(relOptSchema, "myTable", table.getRowType(typeFactory)) {}
      EnumerableTableScan.create(cluster, relOptTable)
    })

    // W H E N
    val actual = RelOptUtil.dumpPlan("", selectAllFromMyTable, SqlExplainFormat.TEXT, SqlExplainLevel.DIGEST_ATTRIBUTES)

    // T H E N
    Util.toLinux(actual) shouldBe "EnumerableTableScan(table=[[myTable]])\n"

  }

  it should "return a JsonTableScan" in {

    // G I V E N
//    val selectAllFromMyTable = Frameworks.withPlanner((cluster: RelOptCluster, relOptSchema: RelOptSchema, schemaPlus: SchemaPlus) => {
//      // "SELECT * FROM myTable"
//      val asset = """{"_id":"5c5f1f313fcc6e3084fbe65e","index":0,"guid":"f3b5960b-f3e1-4556-9a5d-f552afe204e7","isActive":true,"balance":"$2,809.92","picture":"http://placehold.it/32x32","age":28,"eyeColor":"blue","personalinfo":{"name":"Elliott Kaufman","gender":"male","phone":"+1 (858) 421-2925","email":"elliottkaufman@spacewax.com","address":"952 Cropsey Avenue, Tyro, Guam, 1787","company":{"name":"SPACEWAX"}},"about":"Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.\r\n","registered":"2015-03-28T06:35:33 -01:00","location":{"latitude":78.370719,"longitude":-137.117139},"greeting":"Hello, Elliott Kaufman! You have 5 unread messages.","favoriteFruit":"strawberry"}"""
//      val flatten = new JsonFlatten toMap asset
//      val javaMap = flatten.asJava
//      val planner: RelOptPlanner = cluster.getPlanner
//      planner.addRule(JsonProjectTableScanRule.INSTANCE)
//      planner
//
//      val typeFactory = cluster.getTypeFactory
//      val table = JsonTranslatableTable(flatten)
//      val fieldCount = table.getRowType(typeFactory).getFieldCount
//      val fields = JsonEnumerator.identityList(fieldCount)
//    })

    // W H E N
//    val actual = RelOptUtil.dumpPlan("", selectAllFromMyTable, SqlExplainFormat.TEXT, SqlExplainLevel.DIGEST_ATTRIBUTES)
//
//    // T H E N
//    Util.toLinux(actual) shouldBe "EnumerableTableScan(table=[[myTable]])\n"

  }

}
