// workaround for
// https://groups.google.com/d/topic/simple-build-tool/_bBUQk4dIAE/discussion
// http://togetter.com/li/415266
sourceGenerators in Compile += task(
  nobox.Generate((scalaSource in Compile).value)
)

cleanFiles ++= {
  nobox.Generate((scalaSource in Compile).value)
}

packageSrc in Compile <<= (packageSrc in Compile).dependsOn(compile in Compile)

compile in Compile <<= (compile in Compile).dependsOn(clean)
