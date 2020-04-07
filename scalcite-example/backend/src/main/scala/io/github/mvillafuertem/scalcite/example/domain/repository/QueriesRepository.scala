package io.github.mvillafuertem.scalcite.example.domain.repository

import zio.Has

/**
 * @author Miguel Villafuerte
 */
trait QueriesRepository[T] extends BaseRepository[T]

object QueriesRepository {
  type QueriesRepo[T] = Has[QueriesRepository[T]]
}
