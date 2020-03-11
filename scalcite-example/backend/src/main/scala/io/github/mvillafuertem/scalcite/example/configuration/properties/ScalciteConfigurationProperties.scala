package io.github.mvillafuertem.scalcite.example.configuration.properties

import com.typesafe.config.ConfigFactory
import io.github.mvillafuertem.scalcite.example.configuration.properties.ScalciteConfigurationProperties._

final case class ScalciteConfigurationProperties(
    name: String = ConfigFactory.load().getString(s"$path.name"),
    interface: String = ConfigFactory.load().getString(s"$path.server.interface"),
    port: Int = ConfigFactory.load().getInt(s"$path.server.port")
)

object ScalciteConfigurationProperties {

  private[configuration] val path: String = "application"

}
