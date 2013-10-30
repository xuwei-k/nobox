val scalameter = project.settings(
  fork in test := true,
  resolvers += Opts.resolver.sonatypeSnapshots,
  libraryDependencies ++= {
    if(scalaBinaryVersion.value != "2.10") Nil
    else Seq("com.github.axel22" %% "scalameter" % "0.4-SNAPSHOT" % "test")
  },
  testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")
).settings(Common.commonSettings: _*) dependsOn (LocalRootProject)

