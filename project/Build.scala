import sbt._
import Keys._
import Tests._

object OrientDBEmbedded extends Build {

  lazy val projectSettings = Seq(
    version := "0.1.0",
    organizationName := "Blue Skiron",
    organization := "com.blueskiron",
    name := "orientdb-embedded",
    logBuffered := false,
    scalaVersion := "2.11.7",
    scalacOptions := Seq(
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-deprecation", // warning and location for usages of deprecated APIs
      "-feature", // warning and location for usages of features that should be imported explicitly
      "-unchecked", // additional warnings where generated code depends on assumptions
      "-Xlint", // recommended additional warnings
      "-Ywarn-inaccessible",
      "-Ywarn-dead-code"))

  lazy val root = Project(
    id = "orientdb-embedded",
    base = file("."),
    settings = projectSettings ++ Seq(
      fork := true,
      libraryDependencies ++= Dependencies.all))
}

object Dependencies {

  // Versions
  object Version {
    val akka = "2.4.0"
    val orientDb = "2.1.6"
  }

  // Libraries
  //val specs2core = "org.specs2" %% "specs2-core" % "2.4.14"
  val scalaTest = "org.scalatest" % "scalatest_2.11" % "2.2.4"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % Version.akka
  val akkaLog = "com.typesafe.akka" %% "akka-slf4j" % Version.akka
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % Version.akka
  val orientDbEnterprise = "com.orientechnologies" % "orientdb-enterprise" % Version.orientDb
  val orientDbServer = "com.orientechnologies" % "orientdb-server" % Version.orientDb
  val orientDbCore = "com.orientechnologies" % "orientdb-core" % Version.orientDb
  val orientDbTools = "com.orientechnologies" % "orientdb-tools" % Version.orientDb
  val orientDbGraphdb = "com.orientechnologies" % "orientdb-graphdb" % Version.orientDb withJavadoc ()
  val orientDbDistributed = "com.orientechnologies" % "orientdb-distributed" % Version.orientDb withJavadoc ()

  // Project deps
  val all = Seq(orientDbEnterprise, orientDbServer, orientDbCore, orientDbGraphdb, orientDbDistributed, orientDbTools) ++ Seq(akkaActor, akkaLog, akkaTestkit % Test, scalaTest % Test)

}
