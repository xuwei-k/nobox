sourceGenerators in Compile += task{
  nobox.Generate(sourceManaged.value)
}

sourceGenerators in Compile += task{
  nobox.GenerateList(sourceManaged.value)
}

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.10.1" % "test"
)

scalaVersion := "2.10.3"

crossScalaVersions := List("2.10.3")

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
