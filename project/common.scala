import com.typesafe.sbt.pgp.PgpKeys
import sbt._, Keys._
import sbtrelease.Git
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

object Common {

  private[this] val unusedWarnings = (
    "-Ywarn-unused" ::
    "-Ywarn-unused-import" ::
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
    git.commit(message = "update " + readme, sign = false) ! state.log
    "git diff HEAD^" ! state.log
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

  val Scala211 = "2.11.11"

  val commonSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := Scala211,
    crossScalaVersions := "2.13.0-M1" :: "2.12.3" :: Scala211 :: Nil,
    organization := "com.github.xuwei-k",
    commands += Command.command("updateReadme")(updateReadme),
    resolvers += Opts.resolver.sonatypeReleases,
    resolvers ++= {
      if(scalaVersion.value endsWith "SNAPSHOT"){
        Opts.resolver.sonatypeSnapshots :: Nil
      }else Nil
    },
    incOptions := incOptions.value.withNameHashing(true),
    scalacOptions ++= (
      "-deprecation" ::
      "-unchecked" ::
      "-Xlint" ::
      "-language:existentials" ::
      "-language:higherKinds" ::
      "-language:implicitConversions" ::
      Nil
    ),
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
      setNextVersion,
      commitNextVersion,
      updateReadmeProcess,
      releaseStepCommand("sonatypeReleaseAll"),
      pushChanges
    ),
    trapExit := false
  ) ++ Seq(Compile, Test).flatMap(c =>
    scalacOptions in (c, console) --= unusedWarnings
  )

}
