package io.github.mvillafuertem.scalcite.server.domain.model

import java.util.UUID

/**
 * @author Miguel Villafuerte
 */
final case class Query(uuid: UUID = UUID.randomUUID(), value: String)
