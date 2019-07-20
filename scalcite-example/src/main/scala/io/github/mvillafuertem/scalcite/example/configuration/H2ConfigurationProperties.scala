package io.github.mvillafuertem.scalcite.example.configuration

import com.typesafe.config.ConfigFactory
import io.github.mvillafuertem.scalcite.example.configuration.H2ConfigurationProperties._

/**
  * @author Miguel Villafuerte
  */
case class H2ConfigurationProperties
(url: String = ConfigFactory.load().getString(s"$path.url"),
 user: String = ConfigFactory.load().getString(s"$path.user"),
 password: String = ConfigFactory.load().getString(s"$path.password"),
 initialSize: Integer = ConfigFactory.load().getInt(s"$path.connection.initialPoolSize"),
 maxSize: Integer = ConfigFactory.load().getInt(s"$path.connection.maxPoolSize"),
 connectionTimeoutMillis: Long = ConfigFactory.load().getLong(s"$path.connection.timeoutMillis"),
 validationQuery: String = ConfigFactory.load().getString(s"$path.connection.validationQuery"),
 driverName: String = ConfigFactory.load().getString(s"$path.connection.driver")
)

object H2ConfigurationProperties {

  private val path: String = "infrastructure.h2"

}
