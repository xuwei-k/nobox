import sbt._, Keys._

object Common {

  val commonSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.10.3",
    aggregate := false,
    javaOptions ++= "-Djava.awt.headless=true" +: sys.process.javaVmArguments.filter(
      a => Seq("-Xmx","-Xms","-XX").exists(a.startsWith)
    ),
    resolvers += Opts.resolver.sonatypeReleases,
    incOptions := incOptions.value.withNameHashing(true),
    scalacOptions ++= Seq("-optimize", "-deprecation", "-unchecked", "-Xlint"),
    trapExit := false
  )

}
