# DDDSample (scala port)

This is an ongoing project to port the original DDD Sample app to Scala.

Build with [SBT](http://www.scala-sbt.org/)

## Automatic code reloading

SBT allows for compilation and server restart when source files change:

```bash
$ sbt
> ~;jetty:stop;jetty:start
```

Discussion group: https://groups.google.com/forum/#!forum/dddsample

Development blog: https://citerus.github.io/dddsample-core/

Trello board: https://trello.com/b/PTDFRyxd

[![Build Status](https://travis-ci.org/citerus/dddsample-core.svg?branch=master)](https://travis-ci.org/citerus/dddsample-core)
