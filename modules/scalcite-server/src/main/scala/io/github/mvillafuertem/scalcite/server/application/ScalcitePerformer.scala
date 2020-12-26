package io.github.mvillafuertem.scalcite.server.application

import io.circe.Json
import io.circe.scalcite.blower.ScalciteBlower._
import io.circe.scalcite.flattener.ScalciteFlattener._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.blower.Blower._
import io.github.mvillafuertem.scalcite.flattener.Flattener._
import io.github.mvillafuertem.scalcite.server.api.documentation.ApiJsonCodec._
import io.github.mvillafuertem.scalcite.server.application.QueriesService.ZQueriesApplication
import io.github.mvillafuertem.scalcite.server.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.server.domain.error.ScalciteError.Unknown
import io.github.mvillafuertem.scalcite.server.domain.repository.{ CalciteRepository, ErrorsRepository }
import io.github.mvillafuertem.scalcite.server.domain.{ QueriesApplication, ScalciteApplication }
import io.github.mvillafuertem.scalcite.server.infrastructure.model.ErrorDBO
import io.github.mvillafuertem.scalcite.server.infrastructure.repository.RelationalCalciteRepository.ZCalciteRepository
import io.github.mvillafuertem.scalcite.server.infrastructure.repository.RelationalErrorsRepository.ZErrorsRepository
import zio._
import zio.stream.ZStream

import java.sql.SQLException
import java.util.UUID

private final class ScalcitePerformer(app: QueriesApplication, repository: CalciteRepository, errorsRepository: ErrorsRepository[ErrorDBO])
    extends ScalciteApplication {

  override def performMap(map: collection.Map[String, Any], uuid: UUID*): stream.Stream[Throwable, collection.Map[String, Any]] =
    ZStream
      .fromIterable(uuid)
      .flatMapPar(10)(uuid => app.findByUUID(uuid))
      .flatMapPar(10)(query =>
        repository
          .queryForMap(map, query.value)
          .catchAll(catchErrorsAndInsertInDB)
          .either
          .map(_.fold(_ => Map.empty[String, Any], identity))
      )

  override def performJson(json: Json, uuid: UUID*): stream.Stream[Throwable, Json] =
    for {
      flattened <- flattener(json)
      result    <- ZStream
                     .fromIterable(uuid)
                     .flatMapPar(1)(uuid => app.findByUUID(uuid).tap(_ => UIO(Thread.sleep(1000))))
                     .flatMapPar(1)(query => performQuery(flattened, query.value))
      blowed    <- blower(result)
    } yield blowed

  private def performQuery(json: Json, query: String): stream.Stream[Nothing, Json] =
    repository
      .queryForJson(json, query)
      .catchAll(catchErrorsAndInsertInDB)
      .either
      .map(_.fold(_.asJson, identity))

  private def catchErrorsAndInsertInDB: ScalciteError => stream.Stream[ScalciteError, Nothing] = { error =>
    errorsRepository.insert(ErrorDBO(error.uuid, error.code, error.timestamp)).mapError { case e: SQLException => Unknown(e.getMessage) } *>
      ZStream.fail(error)
  }

  private def flattener(json: Json): stream.Stream[Nothing, Json] =
    ZStream
      .fromEffect(IO.fromEither(json.flatten))
      .either
      .map(_.fold(e => ScalciteError.Unknown(e.getMessage).asJson, identity))

  private def blower(json: Json): stream.Stream[Nothing, Json] =
    ZStream
      .fromEffect(IO.fromEither(json.asJson.blow))
      .either
      .map(_.fold(e => ScalciteError.Unknown(e.getMessage).asJson, identity))
}

object ScalcitePerformer {

  def apply(app: QueriesApplication, repository: CalciteRepository, errorsRepository: ErrorsRepository[ErrorDBO]): ScalciteApplication =
    new ScalcitePerformer(app, repository, errorsRepository)

  type ZScalciteApplication = Has[ScalciteApplication]

  def performMap(map: collection.Map[String, Any], uuid: UUID*): stream.ZStream[ZScalciteApplication, Throwable, collection.Map[String, Any]] =
    stream.ZStream.accessStream(_.get.performMap(map, uuid: _*))

  def performJson(json: Json, uuid: UUID*): stream.ZStream[ZScalciteApplication, Throwable, Json] =
    stream.ZStream.accessStream(_.get.performJson(json, uuid: _*))

  val live: URLayer[ZQueriesApplication with ZCalciteRepository with ZErrorsRepository, ZScalciteApplication] =
    ZLayer.fromServices[QueriesApplication, CalciteRepository, ErrorsRepository[ErrorDBO], ScalciteApplication](ScalcitePerformer.apply)

}
