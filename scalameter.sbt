val scalameter = project.settings(
  fork in test := true,
  libraryDependencies += "com.github.axel22" %% "scalameter" % "0.5-M2" % "test",
  testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")
).settings(Common.commonSettings: _*) dependsOn (LocalRootProject)

