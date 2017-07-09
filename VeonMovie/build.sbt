name := """InterviewMovie"""
organization := "com.kabelo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  guice,
  "org.scalatest" % "scalatest_2.11" % "3.0.0-SNAP5" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
  "com.typesafe.play" % "play_2.11" % "2.4.0",
  "org.mongodb" % "casbah_2.11" % "2.8.2",
  "com.novus" % "salat_2.11" % "1.9.9",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.5.3",
  "net.cloudinsights" %% "play-plugins-salat" % "1.5.9",
  "org.specs2" % "specs2-core_2.11" % "3.9.1" % "test"
)
