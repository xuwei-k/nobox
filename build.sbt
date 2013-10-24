sourceGenerators in Compile += task{
  Generate(sourceManaged.value)
}

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.10.1" % "test"
)

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-optimize", "-deprecation", "-unchecked", "-Xlint")

name := "nobox"

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

initialCommands := "import nobox._"

