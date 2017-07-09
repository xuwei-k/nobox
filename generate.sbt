val generateSources = taskKey[Seq[File]]("generate main source files")

val generateDirName = "generate"

val generateDir = Def.setting {
  (sourceManaged in Compile in LocalRootProject).value / generateDirName
}

lazy val generator = (project in file("generator")).settings(
  generateSources := Def.sequential(
    Def.task {
      IO.delete(generateDir.value)
    },
    Def.task {
      val cp = (fullClasspath in Compile).value
      val _ = (runner in Compile).value.run(
        mainClass = "nobox.Generate",
        classpath = Attributed.data(cp),
        options = Seq(generateDir.value.toString),
        log = streams.value.log
      )
    },
    Def.task {
      (generateDir.value ** "*.scala").get
    }
  ).value
).settings(Common.commonSettings)

sourceGenerators in Compile += (generateSources in generator)

mappings in (Compile, packageSrc) ++= (managedSources in Compile).value.map{ f =>
  // to merge generated sources into sources.jar as well
  (f, f.relativeTo((sourceManaged in Compile).value).get.getPath.replace(generateDirName, "nobox").replace("sbt-buildinfo", "nobox"))
}
