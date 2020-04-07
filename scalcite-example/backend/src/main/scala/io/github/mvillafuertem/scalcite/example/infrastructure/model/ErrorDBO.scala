package io.github.mvillafuertem.scalcite.example.infrastructure.model

import java.util.UUID

import scalikejdbc._

case class ErrorDBO(uuid: UUID, value: String, id: Option[Long] = None)

object ErrorDBO extends SQLSyntaxSupport[ErrorDBO] {

  override val tableName = "scalcitesql"

  def apply(rs: WrappedResultSet) = new ErrorDBO(
    UUID.fromString(rs.string("uuid")),
    rs.string("value"),
    rs.longOpt("id"))

}

