name := "scalcite"

version := "0.1.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.apache.calcite" % "calcite-core" % "1.18.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "io.github.mvillafuertem" %% "mapflablup" % "0.1.1"
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-streams" % "3.3.5" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.23" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.23" % Test

// L O G B A C K
libraryDependencies +=  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test

// S C A L A  L O G G I N G
libraryDependencies +=  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2" % Test