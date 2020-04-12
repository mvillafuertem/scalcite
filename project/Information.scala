import sbt.Keys._
import sbt.{Def, Developer, ScmInfo, _}

object Information {

  val value: Seq[Def.Setting[_]] = Seq(
    organization := "io.github.mvillafuertem",
    description := "Scalcite is a library",
    homepage := Some(url(s"https://github.com/mvillafuertem/scalcite")),
    licenses := List("MIT" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "mvillafuertem",
        "Miguel Villafuerte",
        "mvillafuertem@email.com",
        url("https://github.com/mvillafuertem")
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/mvillafuertem/scalcite"),
        "scm:git@github.com:mvillafuertem/scalcite.git"
      )
    )
  )

}
