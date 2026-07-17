addSbtPlugin("com.github.xuwei-k" % "sbt-root-aggregate" % "0.1.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.5.0")

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
)

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.1")

addSbtPlugin("com.github.scalaprops" % "sbt-scalaprops" % "0.5.3")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.22.0")

addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.12")
