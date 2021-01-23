import com.jsuereth.sbtpgp.SbtPgp.autoImport._
import sbt._, Keys._
import sbtrelease.Git
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import xerial.sbt.Sonatype.autoImport._
import dotty.tools.sbtplugin.DottyPlugin.autoImport.isDotty

object Common {

  private[this] val unusedWarnings = (
    "-Ywarn-unused" ::
    Nil
  )

  val sonatypeURL = "https://oss.sonatype.org/service/local/repositories/"

  val updateReadme: State => State = { state =>
    val extracted = Project.extract(state)
    val scalaV = "2.12"
    val v = extracted get version
    val org = extracted get organization
    val n = "nobox"
    val snapshotOrRelease = if(extracted get isSnapshot) "snapshots" else "releases"
    val readme = "README.md"
    val readmeFile = file(readme)
    val newReadme = Predef.augmentString(IO.read(readmeFile)).lines.map{ line =>
      val matchReleaseOrSnapshot = line.contains("SNAPSHOT") == v.contains("SNAPSHOT")
      if(line.startsWith("libraryDependencies") && matchReleaseOrSnapshot){
        if(line.contains(" %%% ")){
          s"""libraryDependencies += "${org}" %%% "${n}" % "$v""""
        } else {
          s"""libraryDependencies += "${org}" %% "${n}" % "$v""""
        }
      }else if(line.contains(sonatypeURL) && matchReleaseOrSnapshot){
        val sxrIndexHtml = "-sxr.jar/!/index.html"
        val javadocIndexHtml = "-javadoc.jar/!/index.html"
        val baseURL = s"${sonatypeURL}${snapshotOrRelease}/archive/${org.replace('.', '/')}/${n}_${scalaV}/${v}/${n}_${scalaV}-${v}"
        if(line.contains(javadocIndexHtml)){
          s"- [API Documentation](${baseURL}${javadocIndexHtml})"
        }else if (line.contains(sxrIndexHtml)){
          s"- [sxr](${baseURL}${sxrIndexHtml})"
        }else line
      }else line
    }.mkString("", "\n", "\n")
    IO.write(readmeFile, newReadme)
    val git = new Git(extracted get baseDirectory)
    git.add(readme) ! state.log
    git.commit(message = "update " + readme, sign = false, signOff = false) ! state.log
    sys.process.Process("git diff HEAD^") ! state.log
    state
  }

  val updateReadmeProcess: ReleaseStep = Common.updateReadme

  def releaseStepCross[A](key: TaskKey[A]) = ReleaseStep(
    action = { state =>
      val extracted = Project extract state
      extracted.runAggregated(key in Global in extracted.get(thisProjectRef), state)
    },
    enableCrossBuild = true
  )

  val Scala211 = "2.11.12"

  val commonSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := Scala211,
    crossScalaVersions := "3.0.0-M3" :: "2.13.4" :: "2.12.13" :: Scala211 :: Nil,
    organization := "com.github.xuwei-k",
    commands += Command.command("updateReadme")(updateReadme),
    publishTo := sonatypePublishToBundle.value,
    resolvers ++= {
      if(scalaVersion.value endsWith "SNAPSHOT"){
        Opts.resolver.sonatypeSnapshots :: Nil
      }else Nil
    },
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, v)) if v <= 12 =>
          Seq("-Xfuture")
        case _ =>
          Nil
      }
    },
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-language:existentials,higherKinds,implicitConversions"
    ),
    scalacOptions ++= {
      if (isDotty.value) {
        Nil
      } else {
        Seq(
          "-Xlint"
        )
      }
    },
    scalacOptions ++= unusedWarnings,
    releaseCrossBuild := true,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      releaseStepCommandAndRemaining("noboxNative/test"),
      setReleaseVersion,
      commitReleaseVersion,
      updateReadmeProcess,
      tagRelease,
      releaseStepCross(PgpKeys.publishSigned),
      releaseStepCommandAndRemaining("noboxNative/publishSigned"),
      releaseStepCommandAndRemaining("sonatypeBundleRelease"),
      setNextVersion,
      commitNextVersion,
      updateReadmeProcess,
      pushChanges
    ),
    trapExit := false
  ) ++ Seq(Compile, Test).flatMap(c =>
    scalacOptions in (c, console) --= unusedWarnings
  )

}
