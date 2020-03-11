package io.github.mvillafuertem.scalcite.example.api.documentation

import akka.http.scaladsl.model
import io.github.mvillafuertem.scalcite.example.domain.error
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.DuplicatedEntity
import sttp.model.StatusCode
import sttp.tapir.{EndpointOutput, _}

trait ApiErrorMapping extends ApiJsonCodec {

  // B A D  R E Q U E S T
//  private lazy val badRequestDescription = model.StatusCodes.BadRequest.defaultMessage
//  private[api] lazy val statusBadRequest: EndpointOutput.StatusMapping[ScalciteError.MessageParsingError.type] =
//    statusMapping(StatusCode.BadRequest, jsonBody[error.ScalciteError.MessageParsingError.type].example(MessageParsingError).description(badRequestDescription))

  // C O N F L I C T
  private lazy val conflictDescription = model.StatusCodes.Conflict.defaultMessage
  private[api] lazy val statusConflict: EndpointOutput.StatusMapping[error.ScalciteError.DuplicatedEntity.type] =
    statusMapping(StatusCode.Conflict, jsonBody[error.ScalciteError.DuplicatedEntity.type].example(DuplicatedEntity).description(conflictDescription))

  // D E F A U L T
  private lazy val defaultDescription = "Unknown Error"
  private[api] lazy val statusDefault: EndpointOutput.StatusMapping[ScalciteError] =
    statusDefaultMapping(jsonBody[ScalciteError].example(error.ScalciteError.Unknown()).description(defaultDescription))

  // I N T E R N A L  S E R V E R  E R R O R
  private lazy val internalServerErrorDescription = model.StatusCodes.InternalServerError.defaultMessage
  private[api] lazy val statusInternalServerError: EndpointOutput.StatusMapping[error.ScalciteError.Unknown] =
    statusMapping(StatusCode.InternalServerError, jsonBody[error.ScalciteError.Unknown].example(error.ScalciteError.Unknown()).description(internalServerErrorDescription))

  // N O T  F O U N D
//  private lazy val notFoundDescription = model.StatusCodes.NotFound.defaultMessage
//  private[api] lazy val statusNotFound: EndpointOutput.StatusMapping[error.ScalciteError.NonExistentEntityError.type] =
//    statusMapping(StatusCode.NotFound, jsonBody[error.ScalciteError.NonExistentEntityError.type].example(NonExistentEntityError).description(notFoundDescription))


}
