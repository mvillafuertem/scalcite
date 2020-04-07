package io.github.mvillafuertem.scalcite.example.domain

import io.github.mvillafuertem.scalcite.example.domain.QueriesApplication.QueriesApp
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import zio.Has

trait QueriesApplication extends BaseApplication[QueriesApp, Query]

object QueriesApplication {
  type QueriesApp = Has[QueriesApplication]
}
