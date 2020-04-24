package io.github.mvillafuertem.scalcite.example.domain.error

import java.time.Instant
import java.util.UUID

sealed trait ScalciteError extends Product {
  val uuid: UUID = UUID.randomUUID()
  val code: String
  val timestamp: Instant = Instant.now()
}

object ScalciteError {

  case class DuplicatedEntity(override val code: String = "duplicated-entity") extends ScalciteError

  case class Unknown(override val code: String = "unknown", override val uuid: UUID = UUID.randomUUID(), override val timestamp: Instant = Instant.now())
      extends ScalciteError

}
