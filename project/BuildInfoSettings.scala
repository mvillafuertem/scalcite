import com.typesafe.sbt.GitPlugin.autoImport.git
import sbt.Keys._
import sbt.{Def, SettingKey}
import sbtbuildinfo.BuildInfoKeys.buildInfoKeys
import sbtbuildinfo.BuildInfoPlugin.autoImport.{BuildInfoKey, buildInfoObject, BuildInfoOption, buildInfoOptions, buildInfoPackage}

object BuildInfoSettings {

  private val gitCommitString = SettingKey[String]("gitCommit")

  val value: Seq[Def.Setting[_]] = Seq(

    buildInfoObject := "BuildInfoScalcite",
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, gitCommitString),
    buildInfoPackage := s"${organization.value}.scalcite",
    buildInfoOptions ++= Seq(BuildInfoOption.ToJson, BuildInfoOption.BuildTime),
    gitCommitString := git.gitHeadCommit.value.getOrElse("Not Set")

  )

}
