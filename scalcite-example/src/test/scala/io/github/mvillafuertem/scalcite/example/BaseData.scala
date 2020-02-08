package io.github.mvillafuertem.scalcite.example

import java.util.UUID

import io.circe.Json
import io.github.mvillafuertem.scalcite.example.configuration.properties.{CalciteConfigurationProperties, H2ConfigurationProperties}
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import scalikejdbc._
import zio.DefaultRuntime

trait BaseData extends DefaultRuntime
  with AnyFlatSpecLike
  with Matchers with BeforeAndAfterEach {

  val map: collection.Map[String, Any] = collection.Map[String, Any]("boolean" -> true)
  val json: Json = Json.obj("boolean" -> Json.fromBoolean(true))

  val queryBooleanValue = "SELECT `boolean` FROM scalcite"
  val queryStringValue = "SELECT 'favoriteFruit' FROM scalcite"

  val id = 0L

  val uuid1: UUID = UUID.randomUUID()
  val uuid2: UUID = UUID.randomUUID()

  val queryString: Query = Query(uuid1, queryStringValue)
  val queryBoolean: Query = Query(uuid1, queryBooleanValue)
  val queryDBO1: QueryDBO = QueryDBO(uuid1,queryStringValue)
  val queryDBO2: QueryDBO = QueryDBO(uuid2, queryStringValue)

  val h2ConfigurationProperties: H2ConfigurationProperties = H2ConfigurationProperties()
  val calciteConfigurationProperties: CalciteConfigurationProperties = CalciteConfigurationProperties()

  ConnectionPool.add(Symbol(h2ConfigurationProperties.databaseName),
    h2ConfigurationProperties.url,
    h2ConfigurationProperties.user,
    h2ConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = h2ConfigurationProperties.initialSize,
      maxSize = h2ConfigurationProperties.maxSize,
      connectionTimeoutMillis = h2ConfigurationProperties.connectionTimeoutMillis,
      validationQuery = h2ConfigurationProperties.validationQuery,
      driverName = h2ConfigurationProperties.driverName
    )
  )

  ConnectionPool.add(Symbol(calciteConfigurationProperties.databaseName),
    calciteConfigurationProperties.url,
    calciteConfigurationProperties.user,
    calciteConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = calciteConfigurationProperties.initialSize,
      maxSize = calciteConfigurationProperties.maxSize,
      connectionTimeoutMillis = calciteConfigurationProperties.connectionTimeoutMillis,
      validationQuery = calciteConfigurationProperties.validationQuery,
      driverName = calciteConfigurationProperties.driverName)
  )

  override protected def afterEach(): Unit =
    NamedDB(Symbol(h2ConfigurationProperties.databaseName))
      .autoCommit{implicit session => sql"DELETE FROM QUERIES".update().apply()}

}
