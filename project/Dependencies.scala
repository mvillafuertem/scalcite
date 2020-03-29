import sbt._

object Dependencies {

  val `scalcite-core`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    calcite,
    // T E S T
    scalaTest % Test

  )

  val `scalcite-example-backend`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    akkaActorTyped,
    akkaHttp,
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
    zioLogging,
    zioInteropReactiveStreams,
    zioStreams,
    circeGenericExtras,


    // T E S T
    akkaHttpTestkit % Test,
    akkaActorTestkitTyped % Test,
    scalaTest % Test,
    sqlline % Test,

  )

  val `scalcite-example-frontend`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N

    // T E S T

  )

  val `scalcite-docs`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N

    // T E S T

  )

  val `scalcite-blower`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    json4s,

    // T E S T
    scalaTest % Test

  )

  val `scalcite-circe-blower`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    circeCore,
    circeParser,
    jsoniterCore,
    jsoniterMacros,

    // T E S T
    scalaTest % Test

  )

  val `scalcite-flattener`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    json4s,

    // T E S T
    scalaTest % Test

  )

  val `scalcite-circe-flattener`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    circeCore,
    circeParser,
    jsoniterCore,
    jsoniterMacros,

    // T E S T
    scalaTest % Test

  )

  val `scalcite-circe-table`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    circeCore,
    circeParser,

    // T E S T
    scalaTest % Test

  )

  lazy val akkaActorTyped = "com.typesafe.akka" %% "akka-actor-typed" % Version.akka
  lazy val akkaActorTestkitTyped = "com.typesafe.akka" %% "akka-actor-testkit-typed" % Version.akka
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % Version.akkaHttp
  lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttp
  lazy val akkaStreams = "com.typesafe.akka" %% "akka-stream-typed" % Version.akka
  lazy val akkaStreamsTesKit= "com.typesafe.akka" %% "akka-stream-testkit" % Version.akka
  lazy val calcite = "org.apache.calcite" % "calcite-core" % Version.calcite
  lazy val circeCore = "io.circe" %% "circe-core" % Version.circe
  lazy val circeGeneric = "io.circe" %% "circe-generic" % Version.circe
  lazy val circeGenericExtras = "io.circe" %% "circe-generic-extras" % Version.circe
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
  lazy val zioLogging = "dev.zio" %% "zio-logging-slf4j" % Version.zioLogging
  lazy val zioInteropReactiveStreams = "dev.zio" %% "zio-interop-reactivestreams" % Version.zioInteropReactiveStreams
  lazy val zioStreams = "dev.zio" %% "zio-streams" % Version.zio



  object Version {
    val akka = "2.6.3"
    val akkaHttp = "10.1.11"
    val calcite = "1.22.0"
    val circe = "0.13.0"
    val h2 = "1.4.200"
    val json4s = "3.6.7"
    val jsoniter = "2.1.10"
    val logback = "1.2.3"
    val mapflablup = "0.1.1"
    val scalaLogging = "3.9.2"
    // TODO sustituir por mockito
    val scalaMock = "4.4.0"
    val scalaTest = "3.1.1"
    val scalcite = "0.1.1"
    val scalikejdbc = "3.4.1"
    val sqlline = "1.9.0"
    val tapir = "0.12.24"
    val zio = "1.0.0-RC18-2"
    val zioLogging = "0.2.3"
    val zioInteropReactiveStreams = "1.0.3.5-RC6"
  }
}
