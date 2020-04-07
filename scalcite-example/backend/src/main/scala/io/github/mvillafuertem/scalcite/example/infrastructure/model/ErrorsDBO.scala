package io.github.mvillafuertem.scalcite.example.infrastructure.model

import java.util.UUID

import scalikejdbc._

case class ErrorsDBO(uuid: UUID, value: String, id: Option[Long] = None)

object ErrorsDBO extends SQLSyntaxSupport[ErrorsDBO] {

  override val tableName = "scalcitesql"

  def apply(rs: WrappedResultSet) = new ErrorsDBO(
    UUID.fromString(rs.string("uuid")),
    rs.string("value"),
    rs.longOpt("id"))

}

