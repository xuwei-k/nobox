// workaround for
// https://groups.google.com/d/topic/simple-build-tool/_bBUQk4dIAE/discussion
// http://togetter.com/li/415266
val generateSources = taskKey[Seq[File]]("generate main source files")

generateSources := {
  val dir = (scalaSource in Compile).value
  val s = streams.value.log
  s.info("delete " + dir)
  IO.delete(dir)
  val files = nobox.Generate(dir)
  s.info("generated " + files.map(_.getName))
  files
}

compile in Compile <<= (compile in Compile) dependsOn generateSources

cleanFiles ++= {
  nobox.Generate((scalaSource in Compile).value)
}

packageSrc in Compile <<= (packageSrc in Compile).dependsOn(compile in Compile)
