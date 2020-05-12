package io.github.mvillafuertem.scalcite.example.configuration.properties

import com.typesafe.config.{ Config, ConfigFactory }

/**
 * @author Miguel Villafuerte
 */
final case class CalciteConfigurationProperties(
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

  def withUrl(url: String): CalciteConfigurationProperties =
    copy(url = url)

  def withUser(user: String): CalciteConfigurationProperties =
    copy(user = user)

  def withPassword(password: String): CalciteConfigurationProperties =
    copy(password = password)

  def withDatabaseName(databaseName: String): CalciteConfigurationProperties =
    copy(databaseName = databaseName)

  def withInitialSize(initialSize: Integer): CalciteConfigurationProperties =
    copy(initialSize = initialSize)

  def withMaxSize(maxSize: Integer): CalciteConfigurationProperties =
    copy(maxSize = maxSize)

  def withConnectionTimeoutMillis(connectionTimeoutMillis: Long): CalciteConfigurationProperties =
    copy(connectionTimeoutMillis = connectionTimeoutMillis)

  def withValidationQuery(validationQuery: String): CalciteConfigurationProperties =
    copy(validationQuery = validationQuery)

  def withDriverName(driverName: String): CalciteConfigurationProperties =
    copy(driverName = driverName)

}

object CalciteConfigurationProperties {

  def apply(config: Config = ConfigFactory.load().getConfig("infrastructure.calcite")): CalciteConfigurationProperties =
    new CalciteConfigurationProperties(
      url = config.getString("url"),
      user = config.getString("user"),
      password = config.getString("password"),
      databaseName = config.getString("databaseName"),
      initialSize = config.getInt("connection.initialPoolSize"),
      maxSize = config.getInt("connection.maxPoolSize"),
      connectionTimeoutMillis = config.getInt("connection.timeoutMillis"),
      validationQuery = config.getString("connection.validationQuery"),
      driverName = config.getString("connection.driver")
    )

}
