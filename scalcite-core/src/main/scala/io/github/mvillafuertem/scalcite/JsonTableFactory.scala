package io.github.mvillafuertem.scalcite

import java.io.File
import java.util

import io.github.mvillafuertem.scalcite.flattener.core.{JsonParser, MapFlattener}
import org.apache.calcite.model.ModelHandler
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeImpl}
import org.apache.calcite.schema.{SchemaPlus, TableFactory}
import org.apache.calcite.util.Sources

final class JsonTableFactory extends TableFactory[JsonTable] {

  override def create(schema: SchemaPlus,
                      name: String,
                      operand: util.Map[String, AnyRef],
                      rowType: RelDataType): JsonTable = {


    val fileName = operand.get("file").asInstanceOf[String]
    val base = operand.get(ModelHandler.ExtraOperand.BASE_DIRECTORY.camelName).asInstanceOf[File]
    val source = Sources.file(base, fileName)
    val _ = if (rowType != null) RelDataTypeImpl.proto(rowType) else null
    val json = scala.io.Source.fromInputStream(source.openStream()).getLines().mkString
    val map: Map[String, Any] = JsonParser.parse(json)
    val flatten = new MapFlattener(map).flatten
    JsonTranslatableTable(flatten)

  }

}
