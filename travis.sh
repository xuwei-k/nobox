#!/bin/bash

if [[ $TRAVIS_SCALA_VERSION = "ref-211" ]]; then
  sbt '++ 2.11.0-RC4' 'benchmark RefBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "int-211" ]]; then
  sbt '++ 2.11.0-RC4' 'benchmark IntBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "ref-210" ]]; then
  sbt '++ 2.10.4' 'benchmark RefBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "int-210" ]]; then
  sbt '++ 2.10.4' 'benchmark IntBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "test-only-211" ]]; then
  sbt '++ 2.11.0-RC4' test $(if [[ "${TRAVIS_PULL_REQUEST}" == "false" && "${TRAVIS_BRANCH}" == "master" ]]; then echo publish ; fi)
elif [[ $TRAVIS_SCALA_VERSION = "test-only-210" ]]; then
  sbt '++ 2.10.4' test $(if [[ "${TRAVIS_PULL_REQUEST}" == "false" && "${TRAVIS_BRANCH}" == "master" ]]; then echo publish ; fi)
elif [[ $TRAVIS_SCALA_VERSION = "scalameter" ]]; then
  sbt '++ 2.10.4' compile printInfo test scalameter/test checkPackage
elif [[ $TRAVIS_SCALA_VERSION = "2.11.0-SNAPSHOT" ]]; then
  sbt '++ 2.11.0-SNAPSHOT' nobox/compile nobox/test
else
  echo "no match TRAVIS_SCALA_VERSION"
  exit -1
fi

