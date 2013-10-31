#!/bin/bash

if [[ $TRAVIS_SCALA_VERSION = "2.11.0-M6" ]]; then
  sbt generator/generateSources && rm generate.sbt &&
  sbt ++$TRAVIS_SCALA_VERSION nobox/compile printInfo nobox/test nobox/benchmark checkPackage
elif [[ $TRAVIS_SCALA_VERSION = "test-only" ]]; then
  sbt '++ 2.10.3' compile test
else
  sbt ++$TRAVIS_SCALA_VERSION compile printInfo test benchmark scalameter/test checkPackage
fi


