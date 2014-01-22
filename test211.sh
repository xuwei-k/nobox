#!/bin/bash

sbt generator/generateSources && rm generate.sbt &&
sbt '++ 2.11.0-M8' nobox/compile printInfo nobox/test

