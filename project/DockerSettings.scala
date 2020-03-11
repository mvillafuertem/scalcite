import com.typesafe.sbt.SbtNativePackager.autoImport.packageName
import com.typesafe.sbt.packager.docker.Cmd
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{Docker, dockerCommands, dockerExposedPorts}
import sbt.Keys._
import sbt.{Def, _}
import sbtassembly.AssemblyPlugin.autoImport.assembly

object DockerSettings {

  val value: Seq[Def.Setting[_]] = Seq(

    Docker / packageName := s"mvillafuertem/${packageName.value}",

    Docker / version := version.value,

    Docker / mappings ++= Seq(
      (Compile / resourceDirectory).value / "application.conf" -> "/opt/docker/configuration/application.conf",
      (Compile / resourceDirectory).value / "logback.xml" -> "/opt/docker/configuration/logback.xml",
    ),

    dockerExposedPorts ++= Seq(8080),

    dockerCommands := {

      val jarName = (Compile / assembly).value.getName

      Seq(
        Cmd("FROM", "azul/zulu-openjdk-alpine:11-jre"),
        Cmd("RUN", "addgroup", "-S", "scalcite", "&&",
          "adduser", "-S", "-D", "scalcite", "-G", "scalcite", "&&",
          "mkdir", "-p", "/opt/scalcite/configuration"),
        Cmd("VOLUME", "/opt/scalcite/configuration"),
        Cmd("WORKDIR", "/opt/scalcite"),
        Cmd("RUN", "chown", "-R", "scalcite:scalcite", "/opt/scalcite/configuration"),
        Cmd("EXPOSE", "8080"),
        Cmd("COPY", "/opt/docker/configuration/", "/opt/scalcite/configuration/"),
        Cmd("COPY", "/opt/docker/lib/", "/opt/scalcite/"),
        Cmd("CMD",
          s"""
             |[
             |"java",
             |"-Dconfig.file=/opt/scalcite/configuration/application.conf",
             |"-Dlogback.configurationFile=/opt/scalcite/configuration/logback.xml",
             |"-Dcom.sun.management.jmxremote",
             |"-Dcom.sun.management.jmxremote.authenticate=false",
             |"-Dcom.sun.management.jmxremote.ssl=false",
             |"-Dcom.sun.management.jmxremote.local.only=false",
             |"-Dcom.sun.management.jmxremote.port=1099",
             |"-Dcom.sun.management.jmxremote.rmi.port=1099",
             |"-Djava.rmi.server.hostname=127.0.0.1",
             |"-jar",
             |"/opt/scalcite/$jarName"
             |]""".stripMargin.replaceAll("[\\n\\s]", ""))
      )
    }
  )
}