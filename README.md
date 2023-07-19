This repo is mostly for me playing around with the thought of migrating [Hifumi](https://github.com/TiltedToast/hifumi) over to scala

# Getting started

## Prerequisites

-   [sbt](https://www.scala-sbt.org/)
-   [Scala 3](https://www.scala-lang.org/download/)
-   [Java 17](https://www.oracle.com/java/technologies/downloads/#java17)

## Building and running

```bash
sbt assembly
```

```
java -jar target/scala-$SCALA_VERSION/hifumi.jar

# or

scala target/scala-$SCALA_VERSION/hifumi.jar
```

# Development

```bash
sbt

~reStart
```

This will automatically recompile and restart the bot when you make changes to the code. It also ensures that there aren't multiple instances of the bot running at the same time. (unlike `sbt ~run`)
