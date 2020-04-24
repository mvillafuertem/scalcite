package io.github.mvillafuertem.scalcite.example.configuration.properties

import com.typesafe.config.ConfigFactory
import io.github.mvillafuertem.scalcite.example.configuration.properties.CalciteConfigurationProperties._

/**
 * @author Miguel Villafuerte
 */
final case class CalciteConfigurationProperties(
  url: String = ConfigFactory.load().getString(s"$path.url"),
  user: String = ConfigFactory.load().getString(s"$path.user"),
  password: String = ConfigFactory.load().getString(s"$path.password"),
  databaseName: String = ConfigFactory.load().getString(s"$path.databaseName"),
  initialSize: Integer = ConfigFactory.load().getInt(s"$path.connection.initialPoolSize"),
  maxSize: Integer = ConfigFactory.load().getInt(s"$path.connection.maxPoolSize"),
  connectionTimeoutMillis: Long = ConfigFactory.load().getInt(s"$path.connection.timeoutMillis"),
  validationQuery: String = ConfigFactory.load().getString(s"$path.connection.validationQuery"),
  driverName: String = ConfigFactory.load().getString(s"$path.connection.driver")
)

object CalciteConfigurationProperties {

  private val path: String = "infrastructure.calcite"

}
