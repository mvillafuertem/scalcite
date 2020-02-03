import sbt._

object Dependencies {

  val `scalcite-core`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    calcite,
    // T E S T
    scalaTest % Test

  )

  val `scalcite-example`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    akkaHttp,
    akkaStreams,
    h2,
    logback,
    scalaLogging,
    scalikejdbc,
    tapirAkkaHttpServer,
    tapirCore,
    tapirJsonCirce,
    tapirOpenapiCirceYaml,
    tapirOpenapiDocs,
    tapirSttpClient,
    tapirSwaggerUiAkkaHttp,
    zio,
    zioInteropReactiveStreams,
    zioStreams,


    // T E S T
    akkaHttpTestkit % Test,
    akkaStreamsTesKit % Test,
    scalaMock % Test,
    scalaTest % Test,
    sqlline % Test,
    
    // I N T E G R A T I O N  T E S T
    akkaStreamsTesKit % IntegrationTest,
    scalaTest % IntegrationTest

  )

  val `scalcite-docs`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N

    // T E S T

  )

  val `scalcite-blower-core`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    json4s,

    // T E S T
    scalaTest % Test

  )

  val `scalcite-blower-circe`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    circeCore,
    circeParser,
    jsoniterCore,
    jsoniterMacros,

    // T E S T
    scalaTest % Test

  )

  val `scalcite-flattener-core`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    json4s,

    // T E S T
    scalaTest % Test

  )

  val `scalcite-flattener-circe`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    circeCore,
    circeParser,
    jsoniterCore,
    jsoniterMacros,

    // T E S T
    scalaTest % Test

  )

  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % Version.akkaHttp
  lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttp
  lazy val akkaStreams = "com.typesafe.akka" %% "akka-stream-typed" % Version.akka
  lazy val akkaStreamsTesKit= "com.typesafe.akka" %% "akka-stream-testkit" % Version.akka
  lazy val calcite = "org.apache.calcite" % "calcite-core" % Version.calcite
  lazy val circeCore = "io.circe" %% "circe-core" % Version.circe
  lazy val circeGeneric = "io.circe" %% "circe-generic" % Version.circe
  lazy val circeParser = "io.circe" %% "circe-parser" % Version.circe
  lazy val h2 = "com.h2database" % "h2" % Version.h2
  lazy val json4s = "org.json4s" %% "json4s-jackson" % Version.json4s
  lazy val jsoniterCore = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % Version.jsoniter
  lazy val jsoniterMacros = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % Version.jsoniter % "compile-internal"
  lazy val logback = "ch.qos.logback" % "logback-classic" % Version.logback
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % Version.scalaLogging
  // TODO sustituir por mockito
  lazy val scalaMock = "org.scalamock" %% "scalamock" % Version.scalaMock
  lazy val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
  lazy val scalcite = "io.github.mvillafuertem" %% "scalcite" % Version.scalcite
  lazy val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc-streams" % Version.scalikejdbc
  lazy val sqlline = "sqlline" % "sqlline" % Version.sqlline
  lazy val tapirAkkaHttpServer = "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % Version.tapir
  lazy val tapirCore = "com.softwaremill.sttp.tapir" %% "tapir-core" % Version.tapir
  lazy val tapirJsonCirce = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Version.tapir
  lazy val tapirOpenapiCirceYaml = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir
  lazy val tapirOpenapiDocs = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % Version.tapir
  lazy val tapirSttpClient = "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % Version.tapir
  lazy val tapirSwaggerUiAkkaHttp = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % Version.tapir
  lazy val zio = "dev.zio" %% "zio" % Version.zio
  lazy val zioInteropReactiveStreams = "dev.zio" %% "zio-interop-reactivestreams" % Version.zioInteropReactiveStreams
  lazy val zioStreams = "dev.zio" %% "zio-streams" % Version.zio



  object Version {
    // TODO sustituir por mockito
    val akka = "2.6.3"
    val akkaHttp = "10.1.9"
    val calcite = "1.21.0"
    val circe = "0.12.3"
    val h2 = "1.4.200"
    val json4s = "3.6.7"
    val jsoniter = "2.1.6"
    val logback = "1.2.3"
    val mapflablup = "0.1.1"
    val scalaLogging = "3.9.2"
    val scalaMock = "4.4.0"
    val scalaTest = "3.1.0"
    val scalcite = "0.1.1"
    val scalikejdbc = "3.4.0"
    val sqlline = "1.9.0"
    val tapir = "0.12.18"
    val zio = "1.0.0-RC17"
    val zioInteropReactiveStreams = "1.0.3.4-RC1"
  }
}
