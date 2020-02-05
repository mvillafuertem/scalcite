package io.github.mvillafuertem.scalcite.example.infrastructure

import java.util.UUID

import scalikejdbc._

case class QueryDBO(uuid: UUID, value: String, id: Option[Long] = None)

object QueryDBO extends SQLSyntaxSupport[QueryDBO] {

  override val tableName = "scalcitesql"

  def apply(rs: WrappedResultSet) = new QueryDBO(
     UUID.fromString(rs.string("uuid")),
    rs.string("value"),
    rs.longOpt("id"))

}
