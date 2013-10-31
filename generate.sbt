// workaround for
// https://groups.google.com/d/topic/simple-build-tool/_bBUQk4dIAE/discussion
// http://togetter.com/li/415266
val generateSources = taskKey[Unit]("generate main source files")

val generatedSourceDir = "generated"

packageSrc in Compile <<= (packageSrc in Compile).dependsOn(compile in Compile)

val cleanSrc = taskKey[Unit]("clean generated sources")

cleanSrc := IO.delete((scalaSource in Compile).value / generatedSourceDir)

clean <<= clean dependsOn cleanSrc

lazy val generator = project in file("generator") settings(
  libraryDependencies <+= sbtDependency,
  generateSources := {
    val dir = ((scalaSource in Compile in LocalRootProject).value / generatedSourceDir).toString
    val cp = (fullClasspath in Compile).value
    (runner in Compile).value.run("nobox.Generate", Attributed.data(cp), Seq(dir), streams.value.log)
  }
) settings(Common.commonSettings : _*)

compile in Compile <<= (compile in Compile) dependsOn (generateSources in generator)
