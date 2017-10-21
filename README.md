Gradle Hello World [![CircleCI](https://circleci.com/gh/int128/gradle-starter.svg?style=shield)](https://circleci.com/gh/int128/gradle-starter) [![Gradle Status](https://gradleupdate.appspot.com/int128/gradle-starter/status.svg?branch=master)](https://gradleupdate.appspot.com/int128/gradle-starter/status)
==================

This project contains following:

* Example app: `Main.java`
* Example test: `MainSpec.groovy`
* Build script: `build.groovy`
* Running app
* Publishing an artifact to Bintray (and Maven Central)
* Gradle wrapper


Getting Started
---------------

```
./gradlew run
```


Publish to Bintray
------------------

You must provide Bintray credential in `~/.gradle/gradle.properties` as follows:

```properties
bintrayUser=example
bintrayKey=secret
```

```
./gradlew bintrayUpload
```

### CircleCI integration

CircleCI builds the plugin continuously.
It also publishes an artifact if a tag is pushed and following variables are set.

Environment Variable        | Value
----------------------------|------
`$BINTRAY_USER`             | Bintray user name
`$BINTRAY_KEY`              | Bintray key
