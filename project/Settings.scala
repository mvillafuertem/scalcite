import sbt.Keys.{ exportJars, version, _ }
import sbt.{ Def, Tests, _ }

object Settings {

  lazy val scala213               = "2.13.8"
  lazy val scala212               = "2.12.16"
  lazy val supportedScalaVersions = List(scala213, scala212)

  val value: Seq[Def.Setting[_]] = Seq(
    scalaVersion := scala213,
    scalacOptions := {
      val default = Seq(
        "-deprecation",
        "-feature",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-language:postfixOps",
        "-unchecked",
        // "-Xfatal-warnings",
        "-Xlint"
      )
      if (version.value.endsWith("SNAPSHOT"))
        default :+ "-Xcheckinit"
      else
        default
      // check against early initialization
    },
    javaOptions += "-Duser.timezone=UTC",
    Test / fork := false,
    Test / parallelExecution := false,
    Test / testOptions ++= Seq(
      Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/test-reports"),
      Tests.Argument("-oDF")
    ),
    Global / cancelable := true,
    // OneJar
    exportJars := true
  )

  val noPublish: Seq[Def.Setting[_]] = Seq(
    publish / skip := true
  )

  ThisBuild / useCoursier := false

}
