## dswing

``dswing`` is a swing-based library:
* package ``progress`` containing support for displaying progress in lengthy calculations. It builds on
the ``progress`` package in [dcommons](http://github.com/dmarell/dcommons).
* package ``util`` with various nice-to-have classes with shallow functionality.

The library is packaged as an OSGi bundle.

### Release notes

* Version 1.0.7 - 2015-07-19
  * Changed repo URL
* Version 1.0.6 - 2015-07-07
  * Moved to Github
* Version 1.0.5 - 2014-02-08
  * Java 7
  * Changed pom versioning mechanism.
  * Extended site information.
  * Updated versions of dependencies
* Version 1.0 - 2011-10-25  First version released open source.

### Maven usage

```
<repositories>
  <repository>
    <id>marell</id>
    <url>http://marell.se/artifactory/libs-release</url>
  </repository>
</repositories>
...
<dependency>
  <groupId>se.marell</groupId>
  <artifactId>dswing</artifactId>
  <version>1.0.7</version>
</dependency>
```

### Usage example

See ProgressDemoApp.java.