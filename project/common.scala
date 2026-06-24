import com.jsuereth.sbtpgp.SbtPgp.autoImport.*
import sbt.*
import sbt.Keys.*
import sbtrelease.Git
import sbtrelease.ReleasePlugin.autoImport.*
import sbtrelease.ReleaseStateTransformations.*

object Common {
  val isScala3 = Def.setting(
    CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 3)
  )

  private val unusedWarnings = Def.setting(
    scalaBinaryVersion.value match {
      case "2.12" =>
        Seq(
          "-Ywarn-unused",
        )
      case _ =>
        Seq(
          "-Wunused:imports",
        )
    }
  )

  val updateReadme: State => State = { state =>
    val extracted = Project.extract(state)
    val v = extracted.get(version)
    val org = extracted.get(organization)
    val n = "nobox"
    val readme = "README.md"
    val readmeFile = file(readme)
    val newReadme = IO
      .readLines(readmeFile)
      .map { line =>
        val matchReleaseOrSnapshot = line.contains("SNAPSHOT") == v.contains("SNAPSHOT")
        if (line.startsWith("libraryDependencies") && matchReleaseOrSnapshot) {
          if (line.contains(" %%% ")) {
            s"""libraryDependencies += "${org}" %%% "${n}" % "$v""""
          } else {
            s"""libraryDependencies += "${org}" %% "${n}" % "$v""""
          }
        } else line
      }
      .mkString("", "\n", "\n")
    IO.write(readmeFile, newReadme)
    val git = new Git(extracted.get(baseDirectory))
    git.add(readme) ! state.log
    git.commit(message = "update " + readme, sign = false, signOff = false) ! state.log
    sys.process.Process("git diff HEAD^") ! state.log
    state
  }

  val updateReadmeProcess: ReleaseStep = Common.updateReadme

  val commonSettings: Seq[Def.Setting[?]] = Seq(
    organization := "com.github.xuwei-k",
    commands += Command.command("updateReadme")(updateReadme),
    publishTo := (if (isSnapshot.value) None else localStaging.value),
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
      scalaBinaryVersion.value match {
        case "2.12" =>
          Seq(
            "-Xsource:3",
            "-Xlint"
          )
        case "2.13" =>
          Seq(
            "-Xsource:3-cross",
            "-Xlint"
          )
        case _ =>
          Nil
      }
    },
    scalacOptions ++= unusedWarnings.value,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      updateReadmeProcess,
      tagRelease,
      releaseStepCommandAndRemaining(PgpKeys.publishSigned.key.label),
      releaseStepCommandAndRemaining("sonaRelease"),
      setNextVersion,
      commitNextVersion,
      updateReadmeProcess,
      pushChanges
    ),
    trapExit := false
  ) ++ Seq(Compile, Test).flatMap(c => c / console / scalacOptions --= unusedWarnings.value)

}
