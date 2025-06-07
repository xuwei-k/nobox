addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.1")

addSbtPlugin("com.github.scalaprops" % "sbt-scalaprops" % "0.5.1")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.19.0")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")

addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")

addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.8")
