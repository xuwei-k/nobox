#!/bin/bash

sbt generator/generateSources && rm generate.sbt &&
sbt '++ 2.11.0-M7' nobox/compile printInfo nobox/test

