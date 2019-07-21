import sbt.Keys.{exportJars, version, _}
import sbt.{Def, Tests, _}

object Settings {

  val value: Seq[Def.Setting[_]] = Seq(

    scalaVersion := "2.12.8",

    scalacOptions := {
      val default = Seq(
        "-deprecation",
        "-feature",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-language:postfixOps",
        "-unchecked",
        // "-Xfatal-warnings",
        "-Xlint",
      )
      if (version.value.endsWith("SNAPSHOT")) {
        default :+ "-Xcheckinit"
      } else {
        default
      } // check against early initialization
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
  
  val noAssemblyTest: Seq[Def.Setting[_]] = Seq(
    //assembly / test := {}
  )


}
