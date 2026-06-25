import Common.isScala3
import java.lang.management.ManagementFactory
import scala.collection.JavaConverters._

val scalaVersions = Seq("3.3.8", "2.13.18", "2.12.21")

val jvmAndJsSetting = Def.settings(
  scalacOptions ++= {
    if (scalaVersion.value.startsWith("3.3.")) {
      Seq(
        "-Yfuture-lazy-vals",
        "-release:11"
      )
    } else {
      Nil
    }
  },
  (Test / unmanagedSourceDirectories) += {
    (projectMatrixBaseDirectory.value / "src/test/jvm_js").getAbsoluteFile
  }
)

lazy val nobox = projectMatrix
  .in(file("."))
  .enablePlugins(BuildInfoPlugin)
  .defaultAxes()
  .settings(
    Common.commonSettings,
    scalapropsCoreSettings,
    libraryDependencies ++= Seq(
      "com.github.scalaprops" %%% "scalaprops" % "0.11.0" % "test",
    ),
    (Compile / unmanagedResources) += (LocalRootProject / baseDirectory).value / "LICENSE.txt",
    name := "nobox",
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    console / initialCommands := "import nobox._",
    startYear := Some(2013),
    description := "immutable primitive array wrapper for Scala",
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
        <tag>{gitTagOrHash.value}</tag>
      </scm>
    ),
    pomPostProcess := { node =>
      import scala.xml._
      import scala.xml.transform._
      def stripIf(f: Node => Boolean) = new RewriteRule {
        override def transform(n: Node) =
          if (f(n)) NodeSeq.Empty else n
      }
      val stripTestScope = stripIf { n => n.label == "dependency" && (n \ "scope").text == "test" }
      new RuleTransformer(stripTestScope).transform(node)(0)
    },
    buildInfoKeys := Seq[BuildInfoKey](
      organization,
      name,
      version,
      scalaVersion,
      sbtVersion,
      licenses
    ),
    buildInfoPackage := "nobox",
    buildInfoObject := "BuildInfoNobox",
    (Compile / sourceGenerators) += Def.task {
      val dir = generateDir.value
      scala.util.Using.resource(
        new java.net.URLClassLoader(
          (generator / Compile / fullClasspath).value.map(_.data.toURI.toURL).toArray,
          ClassLoader.getPlatformClassLoader
        ),
      ) { loader =>
        loader
          .loadClass("nobox.Generate")
          .getConstructor()
          .newInstance()
          .asInstanceOf[
            { def main(dir: File): Array[File] }
          ]
          .main(dir)
          .toSeq
      }
    },
    checkPackage := {
      println(IO.read(makePom.value))
      println()
      IO.withTemporaryDirectory { dir =>
        IO.unzip((Compile / packageSrc).value, dir).map(f => f.getName -> f.length).foreach(println)
      }
    },
    benchmark := {
      val args = benchmarkArgsParser.parsed
      val ((classes, names), sizes) = args
      val cp = (Test / fullClasspath).value
      val r = (Test / runner).value
      classes.foreach { clazz =>
        def run(s: Option[String]) = r.run(
          "nobox." + clazz,
          Attributed.data(cp),
          s.toList ++ names,
          streams.value.log
        )
        if (sizes.isEmpty) run(None)
        else sizes.foreach(run)
      }
    }
  )
  .jsPlatform(
    scalaVersions,
    Def.settings(
      jvmAndJsSetting,
      scalacOptions ++= {
        val a = (LocalRootProject / baseDirectory).value.toURI.toString
        val g = "https://raw.githubusercontent.com/xuwei-k/nobox/" + gitTagOrHash.value
        if (isScala3.value) {
          Seq(s"-scalajs-mapSourceURI:$a->$g/")
        } else {
          Seq(s"-P:scalajs:mapSourceURI:$a->$g/")
        }
      }
    ),
  )
  .jvmPlatform(
    scalaVersions,
    Def.settings(
      jvmAndJsSetting,
      javaOptions ++= "-Djava.awt.headless=true" +: ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList
        .filter(a => Seq("-Xmx", "-Xms", "-XX").exists(a.startsWith))
    ),
  )
  .nativePlatform(
    scalaVersions,
    Def.settings(
      scalapropsNativeSettings,
    ),
  )

lazy val notPublish = Seq(
  publish / skip := true,
  publishArtifact := false,
  publish := {},
  publishLocal := {},
  PgpKeys.publishSigned := {},
  PgpKeys.publishLocalSigned := {}
)

lazy val root = project
  .in(file("."))
  .aggregate(
    generator,
  )
  .aggregate(
    nobox.projectRefs *,
  )
  .settings(
    Common.commonSettings,
    notPublish,
    TaskKey[Unit]("testSequential") :=
      Def
        .sequential(
          nobox.projectRefs.map(_ / Test / test)
        )
        .value,
    Compile / scalaSource := baseDirectory.value / "dummy",
    Test / scalaSource := baseDirectory.value / "dummy"
  )

lazy val gitTagOrHash = Def.setting {
  if (isSnapshot.value) {
    sys.process.Process("git rev-parse HEAD").lineStream_!.head
  } else {
    "v" + version.value
  }
}

lazy val benchmark = inputKey[Unit]("benchmark")

lazy val benchmarkClasses = Set("IntBenchmark", "RefBenchmark")

lazy val seqMethods = classOf[Seq[?]].getMethods.map(_.getName).filterNot(_.contains('$')).toSet

lazy val benchmarkArgsParser = {
  import sbt.complete.Parser._
  import sbt.complete.Parsers._
  val classes0 =
    (token(Space) ~> benchmarkClasses.map(token(_)).reduceLeft(_ | _)).* !!! "please input Benchmark classes"
  // run all benchmark when does not specified any benchmark class name
  val classes = classes0.map(c => if (c.isEmpty) benchmarkClasses else c)
  val names = (token(Space) ~> ScalaID.examples(seqMethods)).* !!! "please input method names"
  val size = (token(Space) ~> NatBasic.examples().map(s => Option(s.toString))).* !!! "please input array size"
  (classes ~ names ~ size)
}

lazy val checkPackage = taskKey[Unit]("show pom.xml and sources.jar")

lazy val generateDirName = "generate"

lazy val generateDir = Def.setting {
  (Compile / sourceManaged).value / generateDirName
}

lazy val generator = project
  .in(file("generator"))
  .settings(
    Common.commonSettings,
    notPublish
  )
