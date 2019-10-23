import mdoc.MdocPlugin.autoImport._
import sbt.Keys.{baseDirectory, sbtVersion, scalaVersion, version}
import sbt._

object MdocSettings {

  val value: Seq[Def.Setting[_]] = Seq(

    mdocIn := baseDirectory.value / "src/main/mdoc/",

    mdocOut := file("."),

    mdocVariables := Map(
      "VERSION" -> version.value,
      "SCALA_VERSION" -> scalaVersion.value,
      "SBT_VERSION" -> sbtVersion.value
    )

  )

}
