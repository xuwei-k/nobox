val packageSxr = TaskKey[File]("packageSxr")

def sxrSetting(c: Configuration): Seq[Def.Setting[_]] = {
  val disableSxr = sys.props.isDefinedAt("disable_sxr")
  println("disable_sxr = " + disableSxr)
  if(disableSxr){
    Nil
  }else{
    Defaults.packageTaskSettings(
      packageSxr in c, (crossTarget in c).map{ dir =>
        Path.allSubpaths(dir / "classes.sxr").toSeq
      }
    ) ++ Seq[Def.Setting[_]](
      resolvers += "bintray/paulp" at "https://dl.bintray.com/paulp/maven",
      addCompilerPlugin("org.improving" %% "sxr" % "1.0.1"),
      packageSxr in c <<= (packageSxr in c).dependsOn(compile in c),
      packagedArtifacts <++= Classpaths.packaged(Seq(packageSxr in c)),
      artifacts <++= Classpaths.artifactDefs(Seq(packageSxr in c)),
      artifactClassifier in packageSxr := Some("sxr"),
      scalacOptions in c <+= scalaSource in c map {
        "-P:sxr:base-directory:" + _.getAbsolutePath
      }
    )
  }
}

sxrSetting(Compile)
