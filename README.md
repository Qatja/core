## Qatja

[![Build Status](https://travis-ci.org/Qatja/core.svg?branch=master)](https://travis-ci.org/Qatja/core)

[![Download](https://api.bintray.com/packages/wetcat/Qatja/core/images/download.svg)](https://bintray.com/wetcat/Qatja/core/_latestVersion)

MQTT Java Library, conforms to MQTT 3.1.1

This library is barebones, it contains no platform specific implementations such as threading or connectionhandling. If you plan on using this for Android projects you should look at [Qatja Android](https://github.com/Qatja/qatja-android) which uses a predefined service.

### Installation

Add the following to your build.gradle file

#### Gradle

```groovy
dependencies {
    compile 'se.wetcat.qatja:core:1.0.1'
}
```

#### Maven

```xml
<dependency>
  <groupId>se.wetcat.qatja</groupId>
  <artifactId>core</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

### Acknowledgements

Based on the work by [Andreas Göransson](https://github.com/agoransson) and [David Cuartielles](https://github.com/dcuartielles) in the book ["Professional Android Open Accessory programming with Arduino"](https://github.com/aoabook) and later [mqtt4processing](https://github.com/agoransson/mqtt4processing) and [mqtt4android](https://github.com/agoransson/mqtt4android).

