#!/bin/bash

if [[ $TRAVIS_SCALA_VERSION = "2.11.0-M6" ]]; then
  sbt generator/generateSources printInfo && rm generate.sbt &&
  sbt ++$TRAVIS_SCALA_VERSION nobox/compile 'nobox/test:runMain nobox.Info all' nobox/test nobox/benchmark checkPackage
elif [[ $TRAVIS_SCALA_VERSION = "test-only" ]]; then
  sbt '++ 2.10.3' compile test
else
  sbt ++$TRAVIS_SCALA_VERSION printInfo compile 'test:runMain nobox.Info all' test benchmark scalameter/test checkPackage
fi


