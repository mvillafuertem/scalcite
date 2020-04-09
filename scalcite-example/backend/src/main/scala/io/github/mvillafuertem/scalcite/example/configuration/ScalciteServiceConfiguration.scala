package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.configuration.AkkaConfiguration.ZAkkaConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.ApiConfiguration.ZApiConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.ApplicationConfiguration.ZApplicationConfiguration
import zio.ZLayer


trait ScalciteServiceConfiguration {


  private val value: ZLayer[Any, Nothing, ZApplicationConfiguration] = InfrastructureConfiguration.live >>> ApplicationConfiguration.live
  private val value1: ZLayer[Any, Throwable, ZAkkaConfiguration] = InfrastructureConfiguration.live >>> AkkaConfiguration.live
  val ZScalciteEnv: ZLayer[Any, Throwable, ZApiConfiguration with ZAkkaConfiguration] =
    ((value ++ value1 ) >>> ApiConfiguration.live) ++ value1


}