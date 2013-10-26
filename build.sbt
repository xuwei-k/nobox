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

val sizeParser = {
  import sbt.complete.Parser._
  import sbt.complete.Parsers._
  val examples = List.iterate(10000, 6)(_ * 8).map(_.toString)
  val msg = "invalid input. please input benchmark array size"
  (Space ~> NatBasic.examples(examples: _*)).? !!! msg
}

benchmark := {
  val size = sizeParser.parsed.map(_.toString).toList
  val cp = (fullClasspath in Test).value
  (runner in Test).value.run("nobox.Benchmark", Build.data(cp), size, streams.value.log)
}

// scalameter configuration

fork in test := true

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.github.axel22" %% "scalameter" % "0.4-SNAPSHOT"

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")
