package io.github.mvillafuertem.scalcite.example.domain.repository

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.infrastructure.QueryDBO

/**
 * @author Miguel Villafuerte
 */
trait QueriesRepository[F[_]] {

  def insert(queryDBO: QueryDBO): F[_]

  def delete(uuid: UUID): F[_]

  def findById(id: Long): F[_]
}
