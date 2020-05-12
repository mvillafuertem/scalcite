package io.github.mvillafuertem.scalcite.example.configuration.properties

import com.typesafe.config.{ Config, ConfigFactory }

/**
 * @author Miguel Villafuerte
 */
case class H2ConfigurationProperties(
  url: String,
  user: String,
  password: String,
  databaseName: String,
  initialSize: Integer,
  maxSize: Integer,
  connectionTimeoutMillis: Long,
  validationQuery: String,
  driverName: String
) {

  def withUrl(url: String): H2ConfigurationProperties =
    copy(url = url)

  def withUser(user: String): H2ConfigurationProperties =
    copy(user = user)

  def withPassword(password: String): H2ConfigurationProperties =
    copy(password = password)

  def withDatabaseName(databaseName: String): H2ConfigurationProperties =
    copy(databaseName = databaseName)

  def withInitialSize(initialSize: Integer): H2ConfigurationProperties =
    copy(initialSize = initialSize)

  def withMaxSize(maxSize: Integer): H2ConfigurationProperties =
    copy(maxSize = maxSize)

  def withConnectionTimeoutMillis(connectionTimeoutMillis: Long): H2ConfigurationProperties =
    copy(connectionTimeoutMillis = connectionTimeoutMillis)

  def withValidationQuery(validationQuery: String): H2ConfigurationProperties =
    copy(validationQuery = validationQuery)

  def withDriverName(driverName: String): H2ConfigurationProperties =
    copy(driverName = driverName)

}

object H2ConfigurationProperties {

  def apply(config: Config = ConfigFactory.load().getConfig("infrastructure.h2")): H2ConfigurationProperties =
    new H2ConfigurationProperties(
      url = config.getString("url"),
      user = config.getString("user"),
      password = config.getString("password"),
      databaseName = config.getString("databaseName"),
      initialSize = config.getInt("connection.initialPoolSize"),
      maxSize = config.getInt("connection.maxPoolSize"),
      connectionTimeoutMillis = config.getLong("connection.timeoutMillis"),
      validationQuery = config.getString("connection.validationQuery"),
      driverName = config.getString("connection.driver")
    )

}
