val scalameter = project.settings(
  fork in test := true,
  libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.6" % "test",
  testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")
).settings(Common.commonSettings: _*) dependsOn (LocalRootProject)

