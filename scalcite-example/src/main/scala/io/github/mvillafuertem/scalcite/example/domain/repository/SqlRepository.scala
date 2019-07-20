package io.github.mvillafuertem.scalcite.example.domain.repository

import io.github.mvillafuertem.scalcite.example.domain.model.ScalciteSql

/**
  * @author Miguel Villafuerte
  */
trait SqlRepository[F[_,_]] {

  def findById(): F[Map[String, Any], _]

  def insert(scalciteSql: ScalciteSql): F[Map[String, Any], _]

  def delete(): F[Map[String, Any], _]

}
