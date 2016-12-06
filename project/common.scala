import sbt._, Keys._

object Common {

  private[this] val unusedWarnings = (
    "-Ywarn-unused" ::
    "-Ywarn-unused-import" ::
    Nil
  )

  val Scala211 = "2.11.8"

  val commonSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := Scala211,
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
    incOptions := incOptions.value.withNameHashing(true),
    scalacOptions ++= (
      "-deprecation" ::
      "-unchecked" ::
      "-Xlint" ::
      "-language:existentials" ::
      "-language:higherKinds" ::
      "-language:implicitConversions" ::
      Nil
    ),
    scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
      case Some((2, v)) if v >= 11 => unusedWarnings
    }.toList.flatten,
    trapExit := false
  ) ++ Seq(Compile, Test).flatMap(c =>
    scalacOptions in (c, console) ~= {_.filterNot(unusedWarnings.toSet)}
  )

}
