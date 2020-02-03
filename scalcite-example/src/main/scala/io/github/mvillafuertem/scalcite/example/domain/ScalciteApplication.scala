package io.github.mvillafuertem.scalcite.example.domain

import akka.stream.scaladsl.Source

/**
  * @author Miguel Villafuerte
  */
trait ScalciteApplication[F[_,_]] {

  def perform(id: Long, json: String): Source[String, _]


  def performJson(id: Long, json: String): Source[String, _]

}
