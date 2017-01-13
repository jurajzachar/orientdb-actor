import sbt._
import Keys._

object Dependencies {

  object Version {
    val logback = "1.1.8"
    val akka = "2.4.14"
    val gremlin = "2.6.0"
    val orientDb = "2.2.13"
    val scalaTest = "3.0.1"
  }

  val logbackClassic = "ch.qos.logback" % "logback-classic" % Version.logback
  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
  val scalactic = "org.scalactic" %% "scalactic" % "3.0.1"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % Version.akka
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % Version.akka
  val akkaLog = "com.typesafe.akka" %% "akka-slf4j" % Version.akka
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % Version.akka
  val orientDbServer = "com.orientechnologies" % "orientdb-server" % Version.orientDb
  val orientDbClient = "com.orientechnologies" % "orientdb-client" % Version.orientDb
  val orientDbCore = "com.orientechnologies" % "orientdb-core" % Version.orientDb
  val orientDbTools = "com.orientechnologies" % "orientdb-tools" % Version.orientDb
  val orientDbGraphdb = "com.orientechnologies" % "orientdb-graphdb" % Version.orientDb withJavadoc
  val blueprintsCore = "com.tinkerpop.blueprints" % "blueprints-core" % Version.gremlin
  val orientDbDistributed = "com.orientechnologies" % "orientdb-distributed" % Version.orientDb withJavadoc
  val orientDbEnterprise =  "com.orientechnologies" % "orientdb-enterprise" % "2.2.0-beta" withJavadoc //is this still needed?
  val gremlinGroovy = "com.tinkerpop.gremlin" % "gremlin-groovy" % Version.gremlin
  val gremlinJava = "com.tinkerpop.gremlin" % "gremlin-java" % Version.gremlin
  //experimental mpollmeier gremlin-scala + orientdb-gremlin (the latter now supported by the Orient team)
  //val gremlinScala = "com.michaelpollmeier" %% "gremlin-scala" % "3.2.3.3"
  //val orientDbGremlin = "com.michaelpollmeier" % "orientdb-gremlin" % "3.2.3.0"

  val dependencies = Seq(gremlinGroovy, gremlinJava, blueprintsCore, orientDbServer, orientDbClient, orientDbCore, orientDbTools, orientDbGraphdb, orientDbDistributed) ++ Seq(scalactic, akkaActor, akkaLog, akkaRemote)
  val testDependencies = Seq(akkaTestkit % Test, scalaTest % Test, logbackClassic % Test)

}
