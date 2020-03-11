package io.github.mvillafuertem.scalcite

import java.io.File
import java.util

import io.circe.scalcite.flattener.ScalciteFlattener._
import io.circe.scalcite.table.ScalciteCirceTable
import io.github.mvillafuertem.scalcite.flattener.Flattener._
import org.apache.calcite.model.ModelHandler
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeImpl}
import org.apache.calcite.schema.{SchemaPlus, TableFactory}
import org.apache.calcite.util.Sources

final class JsonTableFactory extends TableFactory[ScalciteTable] {

  override def create(schema: SchemaPlus,
                      name: String,
                      operand: util.Map[String, AnyRef],
                      rowType: RelDataType): ScalciteTable = {


    val fileName = operand.get("file").asInstanceOf[String]
    val base = operand.get(ModelHandler.ExtraOperand.BASE_DIRECTORY.camelName).asInstanceOf[File]
    val source = Sources.file(base, fileName)
    val _ = if (rowType != null) RelDataTypeImpl.proto(rowType) else null
    val json = scala.io.Source.fromInputStream(source.openStream()).getLines().mkString

    (for {
      value <- io.circe.parser.parse(json)
      flattened <- value.flatten

    } yield flattened) match {
      case Left(value) => throw value
      case Right(value) => ScalciteCirceTable(value)
    }
  }

}
