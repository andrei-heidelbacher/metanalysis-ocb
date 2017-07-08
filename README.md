# Metanalysis-OCB

[![Build Status](https://travis-ci.org/andreihh/metanalysis-ocb.svg)](https://travis-ci.org/andreihh/metanalysis-ocb)
[![codecov](https://codecov.io/gh/andreihh/metanalysis-ocb/branch/master/graph/badge.svg)](https://codecov.io/gh/andreihh/metanalysis-ocb)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Features

- an Open-Closed Breaker analysis based on the `metanalysis` code metadata model

## Using Metanalysis

### Environment requirements

In order to use `metanalysis-ocb` you need to have `JDK 1.7` or newer.

### Using the command line

Download the most recently released artifact with dependencies from
[here](https://github.com/andreihh/metanalysis-ocb/releases) and run it:

```java -jar metanalysis-ocb-$version-all help```

## Building

To build this project, run `./gradlew build`.

To build the artifact with dependencies, run the following command after
 building the project: `./gradlew fatJar`. This will create the
`metanalysis-ocb-$version-all.jar` artifact in `metanalysis-ocb/build/libs/`.

## Documentation

To generate the documentation, run `./gradlew javadocJar`.

## Licensing

The code is available under the Apache V2.0 License.
