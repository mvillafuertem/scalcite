package io.github.mvillafuertem.scalcite.console

import com.github.plokhotnyuk.jsoniter_scala.core.{ readFromArray, JsonValueCodec }
import io.circe.Json
import io.circe.scalcite.flattener.ScalciteFlattener
import io.circe.scalcite.table.ScalciteCirceTable
import io.github.mvillafuertem.scalcite.ScalciteTable
import org.apache.calcite.model.ModelHandler
import org.apache.calcite.rel.`type`.{ RelDataType, RelDataTypeImpl }
import org.apache.calcite.schema.{ SchemaPlus, TableFactory }
import org.apache.calcite.util.Sources

import java.io.File
import java.nio.charset.StandardCharsets
import java.util

final class JsonTableFactory extends TableFactory[ScalciteTable] {

  implicit val codec: JsonValueCodec[Json] = ScalciteFlattener.codec

  override def create(schema: SchemaPlus, name: String, operand: util.Map[String, AnyRef], rowType: RelDataType): ScalciteTable = {

    val fileName = operand.get("file").asInstanceOf[String]
    val base     = operand.get(ModelHandler.ExtraOperand.BASE_DIRECTORY.camelName).asInstanceOf[File]
    val source   = Sources.file(base, fileName)
    val _        = if (rowType != null) RelDataTypeImpl.proto(rowType) else null
    val json     = scala.io.Source.fromInputStream(source.openStream()).getLines().mkString
    val value    = readFromArray(json.getBytes(StandardCharsets.UTF_8))
    ScalciteCirceTable(value)
  }

}
