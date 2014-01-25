import sbtrelease._
import ReleaseStateTransformations._

bintrayPublishSettings

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("array", "collection")

bintray.Keys.whoami := "xuwei-k"

releaseSettings

val sonatypeURL =
"https://oss.sonatype.org/service/local/repositories/releases/archive/"

val updateReadme = { state: State =>
  val extracted = Project.extract(state)
  val scalaV = extracted get scalaBinaryVersion
  val v = extracted get version
  val org =  extracted get organization
  val n = extracted get name
  val readme = "README.md"
  val readmeFile = file(readme)
  val newReadme = Predef.augmentString(IO.read(readmeFile)).lines.map{ line =>
    if(line.startsWith("libraryDependencies")){
      s"""libraryDependencies += "${org}" %% "${n}" % "$v""""
    }else if(line contains sonatypeURL){
      s"[API Documentation](${sonatypeURL}${org.replace('.','/')}/${n}_${scalaV}/${v}/${n}_${scalaV}-${v}-javadoc.jar/!/index.html)"
    }else line
  }.mkString("", "\n", "\n")
  IO.write(readmeFile, newReadme)
  Git.add(readme) ! state.log
  Git.commit("update " + readme) ! state.log
  "git diff HEAD^" ! state.log
  state
}

commands += Command.command("updateReadme")(updateReadme)

val updateReadmeProcess: ReleaseStep = updateReadme

val publishSignedStep: ReleaseStep = ReleaseStep{ state =>
  Project.extract(state).runTask(PgpKeys.publishSigned, state)._1
}.copy(enableCrossBuild = true)

ReleaseKeys.releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  updateReadmeProcess,
  tagRelease,
  publishSignedStep,
  setNextVersion,
  commitNextVersion,
  pushChanges
)

publishTo := {
  if(version.value endsWith "SNAPSHOT")
    Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
  else
    Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
}

sonatypeSettings

