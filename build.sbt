val scala3Version = "3.3.0"

lazy val root = project
    .in(file("."))
    .settings(
      name := "hifumi",
      version := "0.1.0-SNAPSHOT",
      scalaVersion := scala3Version,
      libraryDependencies ++= Seq(
        "org.scalameta" %% "munit" % "0.7.29" % Test,
        "net.dv8tion" % "JDA" % "5.0.0-beta.12",
        "io.github.cdimascio" % "dotenv-java" % "3.0.0",
        "org.slf4j" % "slf4j-simple" % "2.0.7"
      ),
      assembly / assemblyJarName := "hifumi.jar",
      assembly / mainClass := Some("Main")
    )

ThisBuild / assemblyMergeStrategy := {
    case PathList("META-INF", _*) => MergeStrategy.discard
    case x                        => MergeStrategy.first
}
