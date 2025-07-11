[![License (3-Clause BSD)](https://img.shields.io/badge/license-BSD%203--Clause-blue.svg?style=flat-square)](https://opensource.org/licenses/BSD-3-Clause)
[![Kotlin](https://img.shields.io/badge/kotlin-2.2.0-7f52ff)](https://kotlinlang.org/)
[![bld](https://img.shields.io/badge/2.3.0-FA9052?label=bld&labelColor=2392FF)](https://rife2.com/bld)
[![Release](https://img.shields.io/github/release/ethauvin/readingtime.svg)](https://github.com/ethauvin/readingtime/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/net.thauvin.erik/readingtime.svg?color=blue)](https://search.maven.org/search?q=g:%22net.thauvin.erik%22%20AND%20a:%22readingtime%22)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fnet%2Fthauvin%2Ferik%2Freadingtime%2Fmaven-metadata.xml&label=snapshot)](https://github.com/ethauvin/readingtime/packages/2260777/versions)


[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ethauvin_readingtime&metric=alert_status)](https://sonarcloud.io/dashboard?id=ethauvin_readingtime)
[![GitHub CI](https://github.com/ethauvin/readingtime/actions/workflows/bld.yml/badge.svg)](https://github.com/ethauvin/readingtime/actions/workflows/bld.yml)
[![CircleCI](https://circleci.com/gh/ethauvin/readingtime/tree/master.svg?style=shield)](https://circleci.com/gh/ethauvin/readingtime/tree/master)

# Estimated Reading Time for Blog Posts, Articles, etc.

A simple implementation of [Medium's Read Time calculation](https://blog.medium.com/read-time-and-you-bc2048ab620c).

## Examples (TL;DR)

```kotlin
import net.thauvin.erik.readingtime.ReadingTime

// ...

val rt = ReadingTime(htmlText)
println(rt.calcEstimatedReadTime()) // eg: 2 min read

```

- View [bld](https://github.com/ethauvin/readingtime/blob/master/examples/bld) or [Gradle](https://github.com/ethauvin/readingtime/blob/master/examples/gradle) Examples

To get the estimated reading time in seconds use the `calcReadingTimeInSec()` function.

## bld

To use with [bld](https://rife2.com/bld), include the following dependency in your [build](https://github.com/ethauvin/readingtime/blob/master/examples/bld/src/bld/java/com/example/ReadingTimeExampleBuild.java) file:

```java
repositories = List.of(MAVEN_CENTRAL, CENTRAL_SNAPSHOTS);

scope(compile)
    .include(dependency("net.thauvin.erik:readingtime:0.9.2"));
```

Be sure to use the [bld Kotlin extension](https://github.com/rife2/bld-kotlin) in your project.

### Gradle, Maven, etc.

To use with [Gradle](https://gradle.org/), include the following dependency in your [build](https://github.com/ethauvin/readingtime/blob/master/examples/gradle/build.gradle.kts) file:

```gradle
repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    implementation("net.thauvin.erik:readingtime:0.9.2")
}
```

Instructions for using with Maven, Ivy, etc. can be found on [Maven Central](https://search.maven.org/search?q=g:%22net.thauvin.erik%22%20AND%20a:%22readingtime%22).

## Properties

The following properties are available:

```kotlin
ReadingTime(
    text,
    wpm = 275,
    postfix = "min read",
    plural = "min read",
    excludeImages = false, 
    extra = 0,
    roundingMode = RoundingMode.HALF_EVEN
)

```

| Property        | Description                                                                                                             |
|:----------------|:------------------------------------------------------------------------------------------------------------------------|
| `text`          | The text to be evaluated. (Required)                                                                                    |
| `wpm`           | The words per minute reading average.                                                                                   |
| `postfix`       | The value to be appended to the reading time.                                                                           |
| `plural`        | The value to be appended if the reading time is more than 1 minute.                                                     |
| `excludeImages` | Images are excluded from the reading time when set.                                                                     |
| `extra`         | Additional seconds to be added to the total reading time.                                                               |
| `roundingMode`  | The [rounding mode](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/RoundingMode.html) to apply. |

## Functions

A couple of useful functions are also available:

```kotlin
ReadingTime.wordCount(htmlText) // Returns the count of words. (HTML stripped)
ReadingTime.imgCount(htmlText) // Returns the count of images. (HTML img tags)
```

## JSP

A JSP tag is also available for easy incorporation into web applications:

```jsp
<%@taglib uri="https://erik.thauvin.net/taglibs/readingtime" prefix="t"%>
<t:readingtime
    wpm="275"
    postfix="min read"
    plural="min read"
    excludeImages="false"
    extra="0">some_text</t:readingtime>
```

None of the attributes are required.

## Java

In addition to setters, a configuration builder is also available:

```java
final ReadingTime rt = new ReadingTime(text);
rt.setPostfix("minute to read");
rt.setPlural("minutes to read");
```

or

```java
final Config config =
        new Config.Builder(text)
                .postfix("minute to read")
                .plural("minutes to read")
                .build();
final ReadingTime rt = new ReadingTime(config);
```

## Contributing

If you want to contribute to this project, all you have to do is clone the GitHub
repository:

```console
git clone git@github.com:ethauvin/readingtime.git
```

Then use [bld](https://rife2.com/bld) to build:

```console
cd readingtime
./bld compile
```

The project has an [IntelliJ IDEA](https://www.jetbrains.com/idea/) project structure. You can just open it after all the dependencies were downloaded and peruse the code.
