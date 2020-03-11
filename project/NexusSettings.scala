import sbt.Keys.resolvers
import sbt._

object NexusSettings {

  val value: Seq[Def.Setting[_]] = Seq(

    resolvers +=
      // ZIO snapshots
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  )

}