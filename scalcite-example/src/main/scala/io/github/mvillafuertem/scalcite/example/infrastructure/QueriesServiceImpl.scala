package io.github.mvillafuertem.scalcite.example.infrastructure

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.domain.repository.QueriesRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.RelationalQueriesRepository.TypeZStream
import zio.{Task, UIO, ZIO}
import zio.stream.{ZSink, ZStream}

final class QueriesServiceImpl(repository: QueriesRepository[ZStream[_, _, _]]) extends QueriesService {

  private def createQuery(query: Query) =
    for {
     input <- Task(QueryDBO(query.uuid, query.value))
     effect: Seq[Any] <- repository.insert(input).run(ZSink.collectAll)
     output <- Task(effect.fold(throw new RuntimeException("error"))(_ => input))

    } yield output

}

trait QueriesService
