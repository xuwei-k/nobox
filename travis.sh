#!/bin/bash

if [[ $TRAVIS_SCALA_VERSION = "ref-211" ]]; then
  ./test211.sh && sbt 'nobox/benchmark RefBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "int-211" ]]; then
  ./test211.sh && sbt 'nobox/benchmark IntBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "ref-210" ]]; then
  sbt 'benchmark RefBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "int-210" ]]; then
  sbt 'benchmark IntBenchmark'
elif [[ $TRAVIS_SCALA_VERSION = "test-only" ]]; then
  sbt '++ 2.10.3' test
elif [[ $TRAVIS_SCALA_VERSION = "scalameter" ]]; then
  sbt '++ 2.10.3' compile printInfo test scalameter/test checkPackage
else
  echo "no match TRAVIS_SCALA_VERSION"
  exit -1
fi

