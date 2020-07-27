[![License (3-Clause BSD)](https://img.shields.io/badge/license-BSD%203--Clause-blue.svg?style=flat-square)](http://opensource.org/licenses/BSD-3-Clause) [![release](https://img.shields.io/github/release/ethauvin/readingtime.svg)](https://github.com/ethauvin/readingtime/releases/latest) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.thauvin.erik/readingtime/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.thauvin.erik/readingtime) [![Download](https://api.bintray.com/packages/ethauvin/maven/readingtime/images/download.svg)](https://bintray.com/ethauvin/maven/readingtime/_latestVersion)

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
    text = "sometext",
    wpm = 275,
    postfix = "min read",
    plural = "min read",
    excludeImages = false
)

```

Property                    | Description                 
:-------------------------- |:-------------------------------------------------------------------
`text`                      | The text to be evaluated.
`wpm`                       | The words per minute reading average.
`postfix`                   | The value to be appended to the reading time.
`plural`                    | The value to be appended if the reading time is more than 1 minute.
`excludeImages`             | Images are excluded from the reading time when set.

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
<t:readingtime postfix="min read" plural="min read" excludeImages="false" wpm="275">some_text</t:readingtime>
```

None of the attributes are required.

Just drop the jar into your `WEB-INF/lib` directory.

### Gradle

To use with [Gradle](https://gradle.org/), include the following dependency in your [build](https://github.com/ethauvin/readingtime/blob/master/examples/build.gradle.kts) file:

```gradle
repositories {
    jcenter()
}

dependencies {
    implementation("net.thauvin.erik:readingtime:0.9.0")
}
```
