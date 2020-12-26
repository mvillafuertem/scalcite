import sbt.Def
import sbt.Keys._
import sbtassembly.AssemblyKeys._
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName
import sbtassembly.{ MergeStrategy, PathList }

object AssemblySettings {

  val value: Seq[Def.Setting[_]] = Seq(
    assembly / assemblyJarName := s"${name.value}-${version.value}.jar",
    assembly / test := {},
    assembly / assemblyMergeStrategy := {
      case "module-info.class"                  => MergeStrategy.last
      case PathList("google", "protobuf", xs @ _*) => MergeStrategy.discard
      case x                                    =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )

}
