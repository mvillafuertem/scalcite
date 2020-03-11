import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.Universal
import sbt.Keys.mappings
import sbt.{Def, _}
import sbtassembly.AssemblyPlugin.autoImport.assembly

object NativePackagerSettings {

  val value: Seq[Def.Setting[_]] = Seq(

    Universal / mappings := {

      val universalMappings: Seq[(File, String)] = (Universal / mappings).value
      val fatJar = (Compile / assembly).value

      // De todas las dependencias solo añadimos los agentes
      val agents = universalMappings filter {
        case (_, name) => name.contains("elastic-apm-agent")
      }

      // Y añadimos el fatJar
      agents :+ (fatJar -> s"lib/${fatJar.getName}")
    }

  )
}
