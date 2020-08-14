import sbt.Def
import sbt.Keys._
import sbtassembly.AssemblyKeys._
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName
import sbtassembly.MergeStrategy

object AssemblySettings {

  val value: Seq[Def.Setting[_]] = Seq(
    assembly / assemblyJarName := s"${name.value}-${version.value}.jar",
    assembly / test := {},
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.last
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )

}
