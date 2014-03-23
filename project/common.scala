import sbt._, Keys._

object Common {

  val commonSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.10.4",
    aggregate := false,
    javaOptions ++= "-Djava.awt.headless=true" +: sys.process.javaVmArguments.filter(
      a => Seq("-Xmx","-Xms","-XX").exists(a.startsWith)
    ),
    resolvers += Opts.resolver.sonatypeReleases,
    resolvers ++= {
      if(scalaVersion.value endsWith "SNAPSHOT"){
        Opts.resolver.sonatypeSnapshots :: Nil
      }else Nil
    },
    scalaBinaryVersion := { if(scalaVersion.value == "2.11.0-SNAPSHOT") "2.11.0-RC3" else scalaBinaryVersion.value },
    incOptions := incOptions.value.withNameHashing(true),
    scalacOptions ++= Seq("-optimize", "-deprecation", "-unchecked", "-Xlint"),
    trapExit := false
  )

}
