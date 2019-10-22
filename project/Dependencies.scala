import sbt._

object Dependencies {

  val `scalcite-core`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    calcite,
    mapflablup,

    // T E S T
    scalaTest % Test

  )

  val `scalcite-example`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N
    akkaStreams,
    logback,
    scalaLogging,
    scalikejdbc,
    h2,

    // T E S T
    akkaStreamsTesKit % Test,
    scalaTest % Test,
    scalaMock % Test,
    sqlline % Test,
    
    // I N T E G R A T I O N  T E S T
    akkaStreamsTesKit % IntegrationTest,
    scalaTest % IntegrationTest

  )

  val `scalcite-docs`: Seq[ModuleID] = Seq(

    // P R O D U C T I O N

    // T E S T

  )

  lazy val akkaStreams = "com.typesafe.akka" %% "akka-stream" % Version.akka
  lazy val akkaStreamsTesKit= "com.typesafe.akka" %% "akka-stream-testkit" % Version.akka
  lazy val calcite = "org.apache.calcite" % "calcite-core" % Version.calcite
  lazy val logback = "ch.qos.logback" % "logback-classic" % Version.logback
  lazy val mapflablup = "io.github.mvillafuertem" %% "mapflablup" % Version.mapflablup
  lazy val scalcite = "io.github.mvillafuertem" %% "scalcite" % Version.scalcite
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % Version.scalaLogging
  lazy val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
  lazy val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc-streams" % Version.scalikejdbc
  lazy val scalaMock = "org.scalamock" %% "scalamock" % Version.scalaMock
  lazy val h2 = "com.h2database" % "h2" % Version.h2
  lazy val sqlline = "sqlline" % "sqlline" % Version.sqlline


  object Version {
    val akka = "2.5.23"
    val akkaHttp = "10.1.9"
    val h2 = "1.4.199"
    val logback = "1.2.3"
    val scalaLogging = "3.9.2"
    val scalaMock = "4.3.0"
    val scalaTest = "3.0.8"
    val scalikejdbc = "3.3.5"
    val mapflablup = "0.1.1"
    val scalcite = "0.1.1"
    val calcite = "1.20.0"
    val sqlline = "1.8.0"
  }
}
