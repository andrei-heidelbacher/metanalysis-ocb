# Metanalysis-OCB

[![](https://jitpack.io/v/andrei-heidelbacher/metanalysis-ocb.svg)](https://jitpack.io/#andrei-heidelbacher/metanalysis-ocb)
[![Build Status](https://travis-ci.org/andrei-heidelbacher/metanalysis-ocb.png)](https://travis-ci.org/andrei-heidelbacher/metanalysis-ocb)
[![codecov](https://codecov.io/gh/andrei-heidelbacher/metanalysis-ocb/branch/master/graph/badge.svg)](https://codecov.io/gh/andrei-heidelbacher/metanalysis-ocb)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Features

- an Open-Closed Breaker analysis based on the `metanalysis` code metadata model
- a Git integration module
- a Java source file parser which extracts Java code metadata

## Using Metanalysis

### Environment requirements

In order to use `metanalysis-ocb` you need to have `JDK 1.8` or newer.

### Using the command line

Download the most recently released artifact with dependencies from
[here](https://github.com/andrei-heidelbacher/metanalysis-ocb/releases) and run
it:

```java -jar metanalysis-ocb-$version-all help```

### Using Gradle

Add the `JitPack` repository to your build file:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Add the dependency:
```groovy
dependencies {
    compile "com.github.andrei-heidelbacher:metanalysis-ocb:$version"
}
```

### Using Maven

Add the `JitPack` repository to your build file:
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Add the dependency:
```xml
<dependencies>
  <dependency>
    <groupId>com.github.andrei-heidelbacher</groupId>
    <artifactId>metanalysis-ocb</artifactId>
    <version>$version</version>
    <scope>compile</scope>
  </dependency>
</dependencies>
```

## Building

To build this project, run `./gradlew build`.

To build the artifact with dependencies, run the following command after
 building the project: `./gradlew fatJar`. This will create the
`metanalysis-ocb-$version-all.jar` artifact in `metanalysis-ocb/build/libs/`.

## Documentation

To generate the documentation, run `./gradlew javadocJar`.

## Licensing

The code is available under the Apache V2.0 License.
