package io.github.mvillafuertem.scalcite.example.infrastructure.model

import java.time.Instant
import java.util.UUID

import scalikejdbc._

case class ErrorDBO(uuid: UUID, code: String, timestamp: Instant, id: Option[Long] = None)

object ErrorDBO extends SQLSyntaxSupport[ErrorDBO] {

  override val tableName = "ERRORS"

  def apply(rs: WrappedResultSet) = new ErrorDBO(UUID.fromString(rs.string("uuid")), rs.string("value"), rs.timestamp("date").toInstant, rs.longOpt("id"))

}
