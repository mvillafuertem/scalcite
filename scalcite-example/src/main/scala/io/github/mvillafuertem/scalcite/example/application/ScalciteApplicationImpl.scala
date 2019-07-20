package io.github.mvillafuertem.scalcite.example.application

import akka.stream.scaladsl.Source
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.example.domain.repository.ScalciteRepository

/**
  * @author Miguel Villafuerte
  */
final class ScalciteApplicationImpl(scalciteRepository: ScalciteRepository[Source]) extends ScalciteApplication[Source] {



}
