package io.github.mvillafuertem.scalcite.example.domain

import io.github.mvillafuertem.scalcite.example.domain.ErrorsApplication.ErrorsApp
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import zio.Has

trait ErrorsApplication extends BaseApplication[ErrorsApp, ScalciteError]

object ErrorsApplication {

  type ErrorsApp = Has[ErrorsApplication]

}
