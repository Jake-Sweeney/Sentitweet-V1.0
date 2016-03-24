name := """Sentitweet V1.0"""

version := "1.0-SNAPSHOT"

maintainer in Linux := "Jake Sweeney"

packageSummary in Linux := "Sentitweet application"

packageDescription := "Tool to preform sentiment analysis on Twitter's tweets based on a user query."

lazy val root = (project in file(".")).enablePlugins(PlayScala, DebianPlugin)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "org.twitter4j" % "twitter4j-core" % "4.0.4",
  "org.twitter4j" % "twitter4j-stream" % "4.0.4",
  "org.twitter4j" % "twitter4j-async" % "4.0.4",
  "org.twitter4j" % "twitter4j-media-support" % "4.0.4",
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "mysql" % "mysql-connector-java" % "5.1.21",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.5.2" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp")),
  "org.apache.spark" %% "spark-core" % "1.3.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
