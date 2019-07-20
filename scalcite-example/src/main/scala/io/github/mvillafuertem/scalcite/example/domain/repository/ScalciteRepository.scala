package io.github.mvillafuertem.scalcite.example.domain.repository

/**
  * @author Miguel Villafuerte
  */
trait ScalciteRepository[F[_,_]] {

  def queryForMap(map: Map[String, Any], sql: String): F[Map[String, Any], _]

}
