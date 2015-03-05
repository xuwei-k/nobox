#!/bin/bash

SCALA_211="2.11.6"
SCALA_210="2.10.5"

if [[ $TRAVIS_SCALA_VERSION = "ref-211" ]]; then
  sbt -Ddisable_sxr "++ ${SCALA_211}" "benchmark RefBenchmark"
elif [[ $TRAVIS_SCALA_VERSION = "int-211" ]]; then
  sbt -Ddisable_sxr "++ ${SCALA_211}" "benchmark IntBenchmark"
elif [[ $TRAVIS_SCALA_VERSION = "ref-210" ]]; then
  sbt -Ddisable_sxr "++ ${SCALA_210}" "benchmark RefBenchmark"
elif [[ $TRAVIS_SCALA_VERSION = "int-210" ]]; then
  sbt -Ddisable_sxr "++ ${SCALA_210}" "benchmark IntBenchmark"
elif [[ $TRAVIS_SCALA_VERSION = "test-only-211" ]]; then
  sbt "++ ${SCALA_211}" test $(if [[ "${TRAVIS_PULL_REQUEST}" == "false" && "${TRAVIS_BRANCH}" == "master" ]]; then echo publish ; fi)
elif [[ $TRAVIS_SCALA_VERSION = "test-only-210" ]]; then
  sbt "++ ${SCALA_210}" test $(if [[ "${TRAVIS_PULL_REQUEST}" == "false" && "${TRAVIS_BRANCH}" == "master" ]]; then echo publish ; fi)
elif [[ $TRAVIS_SCALA_VERSION = "scalameter" ]]; then
  sbt -Ddisable_sxr "++ ${SCALA_210}" compile printInfo test scalameter/test checkPackage
else
  echo "no match TRAVIS_SCALA_VERSION"
  exit -1
fi

