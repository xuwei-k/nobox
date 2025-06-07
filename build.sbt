import sbtcrossproject.CrossType
import scala.collection.JavaConverters._
import java.lang.management.ManagementFactory
import Common.isScala3

lazy val nobox = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CustomCrossType)
  .in(file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    Common.commonSettings,
    scalapropsCoreSettings,
    libraryDependencies ++= (
      ("com.github.scalaprops" %%% "scalaprops" % "0.10.0" % "test") ::
      Nil
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
    printInfo := printInfo.dependsOn(compile).evaluated,
    printInfo := {
      val srcs = (Compile / scalaSource).value
      val files = (srcs ** "*.scala").get.map(f => f -> IO.readLines(f)).sortBy(_._1)
      println("all lines " + files.map(_._2.size).sum)
      files.foreach{ case (file, lines) =>
        println(file.getName + " " + lines.size)
      }
      (Test / runMain).fullInput(" nobox.Info").evaluated
    },
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
    (Compile / sourceGenerators) += Def.sequential(
      Def.task {
        IO.delete(generateDir.value)
      },
      Def.task {
        val cp = (generator / Compile / fullClasspath).value
        val _ = (generator / Compile / runner).value.run(
          mainClass = "nobox.Generate",
          classpath = Attributed.data(cp),
          options = Seq(generateDir.value.toString),
          log = streams.value.log
        )
      },
      Def.task {
        (generateDir.value ** "*.scala").get
      }
    ),
    (Compile / packageSrc / mappings) ++= (Compile / managedSources).value.map{ f =>
      // to merge generated sources into sources.jar as well
      (f, f.relativeTo((Compile / sourceManaged).value).get.getPath.replace(generateDirName, "nobox").replace("sbt-buildinfo", "nobox"))
    },
    checkPackage := {
      println(IO.read(makePom.value))
      println()
      IO.withTemporaryDirectory{ dir =>
        IO.unzip((Compile / packageSrc).value, dir).map(f => f.getName -> f.length) foreach println
      }
    },
    benchmark := {
      val args = benchmarkArgsParser.parsed
      val ((classes, names), sizes) = args
      val cp = (Test / fullClasspath).value
      val r = (Test / runner).value
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
  ).platformsSettings(JVMPlatform, JSPlatform)(
    (Test / unmanagedSourceDirectories) += {
      baseDirectory.value.getParentFile / "jvm_js/src/test/scala/"
    }
  ).jsSettings(
    scalacOptions ++= {
      val a = (LocalRootProject / baseDirectory).value.toURI.toString
      val g = "https://raw.githubusercontent.com/xuwei-k/nobox/" + gitTagOrHash.value
      if (isScala3.value) {
        Seq(s"-scalajs-mapSourceURI:$a->$g/")
      } else {
        Seq(s"-P:scalajs:mapSourceURI:$a->$g/")
      }
    }
  ).jvmSettings(
    javaOptions ++= "-Djava.awt.headless=true" +: ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList.filter(
      a => Seq("-Xmx","-Xms","-XX").exists(a.startsWith)
    )
  ).nativeSettings(
    scalapropsNativeSettings,
  )

lazy val noboxJVM = nobox.jvm
lazy val noboxJS = nobox.js
lazy val noboxNative = nobox.native

lazy val notPublish = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {},
  PgpKeys.publishSigned := {},
  PgpKeys.publishLocalSigned := {}
)

lazy val root = project.in(file(".")).aggregate(
  noboxJVM, noboxJS // exclude noboxNative on purpose
).settings(
  Common.commonSettings,
  notPublish,
  Compile / scalaSource := baseDirectory.value / "dummy",
  Test / scalaSource := baseDirectory.value / "dummy"
)

lazy val gitTagOrHash = Def.setting {
  if(isSnapshot.value) {
    sys.process.Process("git rev-parse HEAD").lineStream_!.head
  } else {
    "v" + version.value
  }
}

lazy val benchmark = inputKey[Unit]("benchmark")

lazy val benchmarkClasses = Set("IntBenchmark", "RefBenchmark")

lazy val seqMethods = classOf[Seq[_]].getMethods.map(_.getName).filterNot(_ contains '$').toSet

lazy val benchmarkArgsParser = {
  import sbt.complete.Parser._
  import sbt.complete.Parsers._
  val classes0 = (token(Space) ~> benchmarkClasses.map(token(_)).reduceLeft(_ | _)).* !!! "please input Benchmark classes"
  // run all benchmark when does not specified any benchmark class name
  val classes = classes0.map(c => if(c.isEmpty) benchmarkClasses else c)
  val names = (token(Space) ~> ScalaID.examples(seqMethods)).* !!! "please input method names"
  val size = (token(Space) ~> NatBasic.examples().map(s => Option(s.toString))).* !!! "please input array size"
  (classes ~ names ~ size)
}

lazy val printInfo = inputKey[Unit]("print each file line counts")

lazy val checkPackage = taskKey[Unit]("show pom.xml and sources.jar")

lazy val generateDirName = "generate"

lazy val generateDir = Def.setting {
  (Compile / sourceManaged).value / generateDirName
}

lazy val generator = (project in file("generator")).settings(
  Common.commonSettings,
  notPublish
)

lazy val CustomCrossType = new sbtcrossproject.CrossType {
  override def projectDir(crossBase: File, projectType: String) =
    crossBase / projectType

  override def projectDir(crossBase: File, projectType: sbtcrossproject.Platform) = {
    val dir = projectType match {
      case JVMPlatform => "jvm"
      case JSPlatform => "js"
      case NativePlatform => "native"
    }
    crossBase / dir
  }

  def shared(projectBase: File, conf: String) =
    projectBase.getParentFile / "src" / conf / "scala"

  override def sharedSrcDir(projectBase: File, conf: String) =
    Some(shared(projectBase, conf))
}
