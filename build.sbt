Common.commonSettings

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
)

crossScalaVersions := List("2.10.4", "2.11.1")

name := "nobox"

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

initialCommands in console := "import nobox._"

startYear := Some(2013)

organization := "com.github.xuwei-k"

description := "immutable primitive array wrapper for Scala"

def gitHash: Option[String] = scala.util.Try(
  sys.process.Process("git rev-parse HEAD").lines_!.head
).toOption

pomExtra := (
<url>https://github.com/xuwei-k/nobox</url>
<developers>
  <developer>
    <id>xuwei-k</id>
    <name>Kenji Yoshida</name>
    <url>https://github.com/xuwei-k</url>
  </developer>
</developers>
<scm>
  <url>git@github.com:xuwei-k/nobox.git</url>
  <connection>scm:git:git@github.com:xuwei-k/nobox.git</connection>
  <tag>{if(isSnapshot.value) gitHash.getOrElse("master") else { "v" + version.value }}</tag>
</scm>
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

val printInfo = inputKey[Unit]("print each file line counts")

printInfo <<= printInfo.dependsOn(compile)

printInfo := {
  val srcs = (scalaSource in Compile).value
  val files = (srcs ** "*.scala").get.map(f => f -> IO.readLines(f)).sortBy(_._1)
  println("all lines " + files.map(_._2.size).sum)
  files.foreach{ case (file, lines) =>
    println(file.getName + " " + lines.size)
  }
  (runMain in Test).fullInput(" nobox.Info").evaluated
}

pomPostProcess := { node =>
  import scala.xml._
  import scala.xml.transform._
  def stripIf(f: Node => Boolean) = new RewriteRule {
    override def transform(n: Node) =
      if (f(n)) NodeSeq.Empty else n
  }
  val stripTestScope = stripIf { n => n.label == "dependency" && (n \ "scope").text == "test" }
  new RuleTransformer(stripTestScope).transform(node)(0)
}
