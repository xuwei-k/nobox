addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.13")

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.1")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.3")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")

addSbtPlugin("com.github.scalaprops" % "sbt-scalaprops" % "0.3.2")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.1.0")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")

addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.0.0")

addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.3.9")
