import Dependencies._

name := """orientdb-actor"""
organization in ThisBuild := "com.blueskiron"
version := "1.0.0"

scalaVersion := "2.11.8"

lazy val root = (project in file("."))

libraryDependencies ++= dependencies ++ testDependencies

//!important as OServerActor should not have multiple parallel incarnations within one JVM
parallelExecution in Test := false
//fork all test tasks
fork := true

scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  "-Xlint", // recommended additional warnings
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  //"-Ywarn-value-discard", // Warn when non-Unit expression results are unused
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code",
  "-language:postfixOps"
)
