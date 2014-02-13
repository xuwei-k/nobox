addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.2")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-language:_")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.1")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.0")

