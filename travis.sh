#!/bin/bash

if [[ $TRAVIS_SCALA_VERSION = "ref-211" ]]; then
  sbt '++ 2.11.0-RC3' 'benchmark RefBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "int-211" ]]; then
  sbt '++ 2.11.0-RC3' 'benchmark IntBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "ref-210" ]]; then
  sbt '++ 2.10.4' 'benchmark RefBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "int-210" ]]; then
  sbt '++ 2.10.4' 'benchmark IntBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "test-only" ]]; then
  sbt '++ 2.10.4' test
elif [[ $TRAVIS_SCALA_VERSION = "scalameter" ]]; then
  sbt '++ 2.10.4' compile printInfo test scalameter/test checkPackage
elif [[ $TRAVIS_SCALA_VERSION = "2.11.0-SNAPSHOT" ]]; then
  sbt '++ 2.11.0-SNAPSHOT' nobox/compile nobox/test
else
  echo "no match TRAVIS_SCALA_VERSION"
  exit -1
fi

