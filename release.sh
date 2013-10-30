sbt clean compile && rm generate.sbt && sbt 'eval "git checkout .".!' 'release cross'

