package io.github.mvillafuertem.scalcite.example.domain.error

import java.util.UUID

import scala.util.control.NoStackTrace

sealed trait ScalciteError extends Product {
  val uuid: UUID = UUID.randomUUID()
  val code: String
}

object ScalciteError {

  case object DuplicatedEntity extends ScalciteError {
    override val code: String = "duplicated-entity"
  }
  case class Unknown(override val code: String = "unknown") extends ScalciteError

  def find(message: String): ScalciteError  = Seq(
    DuplicatedEntity
  ).find(error => error.code.equalsIgnoreCase(message)) match {
    case Some(value) => value
    case None => throw new RuntimeException("NonExistentEntityError") with NoStackTrace
  }

}

