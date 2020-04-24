package io.github.mvillafuertem.scalcite.example

import java.util.UUID

import io.circe.{ Json, JsonObject }
import io.github.mvillafuertem.scalcite.example.configuration.properties.{ CalciteConfigurationProperties, H2ConfigurationProperties }
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import scalikejdbc._
import zio.BootstrapRuntime

trait BaseData extends BootstrapRuntime with AnyFlatSpecLike with Matchers with BeforeAndAfterEach {

  val map: collection.Map[String, Any] = collection.Map[String, Any]("boolean" -> true)
  val json: Json                       = Json.obj("boolean" -> Json.fromBoolean(true))
  val jsonError: Json                  = Json.obj("error" -> Json.fromString("From line 1, column 8 to line 1, column 16: Column 'boolean' not found in any table"))
  val jsonEmpty: Json                  = Json.fromJsonObject(JsonObject.empty)
  val jsonTree: Json                   = Json.obj("boolean.value" -> Json.fromBoolean(true))

  val queryBooleanValue     = "SELECT `boolean` FROM scalcite"
  val queryIntegerValue     = "SELECT `integer` FROM scalcite"
  val queryBooleanTreeValue = "SELECT `boolean.value` FROM scalcite"
  val queryStringValue      = "SELECT `string` FROM scalcite"

  val id = 1L

  val uuid1: UUID = UUID.randomUUID()
  val uuid2: UUID = UUID.randomUUID()
  val uuid3: UUID = UUID.randomUUID()

  val queryString: Query  = Query(uuid1, queryStringValue)
  val queryBoolean: Query = Query(uuid2, queryBooleanValue)
  val queryInteger: Query = Query(uuid3, queryIntegerValue)
  val queryDBO1: QueryDBO = QueryDBO(uuid1, queryStringValue)
  val queryDBO2: QueryDBO = QueryDBO(uuid2, queryStringValue)

  val queryDBO1Expected: QueryDBO = QueryDBO(uuid1, queryStringValue, Some(id))

  val h2ConfigurationProperties: H2ConfigurationProperties           = H2ConfigurationProperties()
  val calciteConfigurationProperties: CalciteConfigurationProperties = CalciteConfigurationProperties()

  ConnectionPool.add(
    Symbol(h2ConfigurationProperties.databaseName),
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

  ConnectionPool.add(
    Symbol(calciteConfigurationProperties.databaseName),
    calciteConfigurationProperties.url,
    calciteConfigurationProperties.user,
    calciteConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = calciteConfigurationProperties.initialSize,
      maxSize = calciteConfigurationProperties.maxSize,
      connectionTimeoutMillis = calciteConfigurationProperties.connectionTimeoutMillis,
      validationQuery = calciteConfigurationProperties.validationQuery,
      driverName = calciteConfigurationProperties.driverName
    )
  )

  override protected def beforeEach(): Unit =
    NamedDB(Symbol(h2ConfigurationProperties.databaseName)).autoCommit { implicit session =>
      sql"""
            DROP TABLE IF EXISTS "QUERIES";
            CREATE TABLE IF NOT EXISTS "QUERIES"
            (
                "UUID" UUID NOT NULL,
                "VALUE" VARCHAR NOT NULL,
                "ID" BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT
            );
            CREATE UNIQUE INDEX IF NOT EXISTS "UUID_INDEX" ON "QUERIES" ("UUID");
           """.execute().apply()
    }

}
