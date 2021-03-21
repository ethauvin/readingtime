[![License (3-Clause BSD)](https://img.shields.io/badge/license-BSD%203--Clause-blue.svg?style=flat-square)](http://opensource.org/licenses/BSD-3-Clause) [![Release](https://img.shields.io/github/release/ethauvin/readingtime.svg)](https://github.com/ethauvin/readingtime/releases/latest) [![Maven Central](https://img.shields.io/maven-central/v/net.thauvin.erik/readingtime.svg?label=maven%20central)](https://search.maven.org/search?q=g:%22net.thauvin.erik%22%20AND%20a:%22readingtime%22)

[![Known Vulnerabilities](https://snyk.io/test/github/ethauvin/readingtime/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/ethauvin/readingtime?targetFile=pom.xml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ethauvin_readingtime&metric=alert_status)](https://sonarcloud.io/dashboard?id=ethauvin_readingtime) [![Build Status](https://travis-ci.com/ethauvin/readingtime.svg?branch=master)](https://travis-ci.com/ethauvin/readingtime) [![CircleCI](https://circleci.com/gh/ethauvin/readingtime/tree/master.svg?style=shield)](https://circleci.com/gh/ethauvin/readingtime/tree/master)

# Estimated Reading Time for Blog Posts, Articles, etc.

A simple Kotlin/Java implementation of [Medium's Read Time calculation](https://blog.medium.com/read-time-and-you-bc2048ab620c).

## Examples (TL;DR)

```kotlin
import net.thauvin.erik.readingtime.ReadingTime

// ...

val rt = ReadingTime(htmlText)
println(rt.calcEstimatedReadTime()) // eg: 2 min read

```

To get the estimated reading time in seconds use the `calcReadingTimeInSec()` function.

 - View [Kotlin](https://github.com/ethauvin/readingtime/blob/master/examples/src/main/kotlin/com/example/ReadingTimeExample.kt) or [Java](https://github.com/ethauvin/readingtime/blob/master/examples/src/main/java/com/example/ReadingTimeSample.java) Examples.



### Properties

The following properties are available:

```kotlin
ReadingTime(
    text = "some_text",
    wpm = 275,
    postfix = "min read",
    plural = "min read",
    excludeImages = false, 
    extra = 0
)

```

Property                    | Description                 
:-------------------------- |:-------------------------------------------------------------------
`text`                      | The text to be evaluated.
`wpm`                       | The words per minute reading average.
`postfix`                   | The value to be appended to the reading time.
`plural`                    | The value to be appended if the reading time is more than 1 minute.
`excludeImages`             | Images are excluded from the reading time when set.
`extra`                     | Additional seconds to be added to the total reading time.

### Functions

A couple of useful functions are also available:

```kotlin
ReadingTime.wordCount(htmlText) // Returns the count of words. (HTML stripped)
ReadingTime.imgCount(htmlText) // Returns the count of images. (HTML img tags)
```

### JSP

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

### Gradle, Maven, etc.

To use with [Gradle](https://gradle.org/), include the following dependency in your [build](https://github.com/ethauvin/readingtime/blob/master/examples/build.gradle.kts) file:

```gradle
dependencies {
    implementation("net.thauvin.erik:readingtime:0.9.0")
}
```

Instructions for using with Maven, Ivy, etc. can be found on [Maven Central](https://search.maven.org/artifact/net.thauvin.erik/readingtime/0.9.0/jar).
