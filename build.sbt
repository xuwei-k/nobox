libraryDependencies ++= Seq(
  // https://github.com/rickynils/scalacheck/pull/64#issuecomment-26704597
  "org.scalacheck" %% "scalacheck" % "1.10.1" % "test" exclude("org.scala-lang", "scala-compiler")
)

scalaVersion := "2.10.3"

crossScalaVersions := List("2.10.3", "2.11.0-M6")

scalacOptions ++= Seq("-optimize", "-deprecation", "-unchecked", "-Xlint")

name := "nobox"

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

initialCommands in console := "import nobox._"

startYear := Some(2013)

organization := "com.github.xuwei-k"

scmInfo := Some(ScmInfo(
  url("https://github.com/xuwei-k/nobox"),
  "scm:git:git@github.com:xuwei-k/nobox.git"
))

description := "immutable primitive array wrapper for Scala"

pomExtra := (
<developers>
  <developer>
    <id>xuwei-k</id>
    <name>Kenji Yoshida</name>
    <url>https://github.com/xuwei-k</url>
  </developer>
</developers>
)

val benchmark = inputKey[Unit]("benchmark")

val benchmarkClasses = Set("IntBenchmark", "RefBenchmark")

val seqMethods = classOf[Seq[_]].getMethods.map(_.getName).filterNot(_ contains '$').toSet

val benchmarkArgsParser = {
  import sbt.complete.Parser._
  import sbt.complete.Parsers._
  val classes0 = (token(Space) ~> benchmarkClasses.map(token(_)).reduceLeft(_ | _)).* !!! "please input Benchmark classes"
  // run all benchmark when does not specified any benchmark class name
  val classes = classes0.map(c => if(c.isEmpty) benchmarkClasses else c)
  val names = (token(Space) ~> ScalaID.examples(seqMethods)).* !!! "please input method names"
  val size = (token(Space) ~> NatBasic.examples().map(s => Option(s.toString))).* !!! "please input array size"
  (classes ~ names ~ size)
}

benchmark := {
  val args = benchmarkArgsParser.parsed
  val ((classes, names), sizes) = args
  val cp = (fullClasspath in Test).value
  val r = (runner in Test).value
  classes.foreach{ clazz =>
    def run(s: Option[String]) = r.run(
      "nobox."+clazz,
      Attributed.data(cp),
      s.toList ++ names,
      streams.value.log
    )
    if(sizes.isEmpty) run(None)
    else sizes.foreach(run)
  }
}

fork in test := true

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.github.axel22" %% "scalameter" % "0.4-SNAPSHOT" % "test"

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

val printInfo = taskKey[Unit]("print each file line counts")

printInfo <<= printInfo.dependsOn(compile)

printInfo := {
  val srcs = (scalaSource in Compile).value
  val files = (srcs ** "*.scala").get.map(f => f -> IO.readLines(f)).sortBy(_._1)
  println("all lines " + files.map(_._2.size).sum)
  files.foreach{ case (file, lines) =>
    println(file.getName + " " + lines.size)
  }
}

javaOptions ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx","-Xms","-XX").exists(a.startsWith)
)
