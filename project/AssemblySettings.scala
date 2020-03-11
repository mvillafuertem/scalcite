import sbt.Def
import sbt.Keys._
import sbtassembly.AssemblyKeys._
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName

object AssemblySettings {

  val value: Seq[Def.Setting[_]] = Seq(

    assembly / assemblyJarName := s"${name.value}-${version.value}.jar",

  )

}
