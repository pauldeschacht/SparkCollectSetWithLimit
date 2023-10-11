name := "SparkCollectSetLimit"
organization := "com.c28n"

version := "0.1"
scalaVersion := "2.12.18"

val sparkVersion = "3.4.1"
val slf4jVersion = "1.7.10"

scalacOptions += "-target:jvm-1.8"
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

resolvers ++= Seq(
  "apache-snapshots" at "https://repository.apache.org/snapshots/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "https://maven.atlassian.com/repository/public/"
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
).map(_.exclude("org.slf4j", "*")) ++ Seq(
  "org.slf4j" % "slf4j-api" % slf4jVersion % "provided"
)

lazy val global = project
  .in(file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "it",
      "org.apache.spark" %% "spark-sql" % sparkVersion % "it",
      "org.scalatest" %% "scalatest" % "3.1.0" % "test,it",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.4" % "it",
    ),
    assemblyPackageScala / assembleArtifact := false,
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case _                        => MergeStrategy.first
    }
  )

Test / fork := true
Test / parallelExecution := false

