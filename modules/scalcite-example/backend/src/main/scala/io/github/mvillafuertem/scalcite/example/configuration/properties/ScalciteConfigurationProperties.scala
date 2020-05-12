package io.github.mvillafuertem.scalcite.example.configuration.properties

import com.typesafe.config.{ Config, ConfigFactory }

final case class ScalciteConfigurationProperties(
  name: String,
  interface: String,
  port: Int
) {

  def withName(name: String): ScalciteConfigurationProperties =
    copy(name = name)

  def withInterface(interface: String): ScalciteConfigurationProperties =
    copy(interface = interface)

  def withPort(port: Int): ScalciteConfigurationProperties =
    copy(port = port)

}

object ScalciteConfigurationProperties {

  def apply(config: Config = ConfigFactory.load().getConfig("application")): ScalciteConfigurationProperties =
    new ScalciteConfigurationProperties(
      name = config.getString("name"),
      interface = config.getString("server.interface"),
      port = config.getInt("server.port")
    )

}
