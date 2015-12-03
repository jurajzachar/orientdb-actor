package test.com.blueskiron.orientdb.embedded.actor

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import com.blueskiron.orientdb.embedded.api.EmbeddedService._
import com.typesafe.config.ConfigFactory

import akka.actor.ActorPath
import akka.actor.ActorSelection.toScala
import akka.actor.ActorSystem

object ClientRun extends App {

  val sys = ActorSystem("client", ConfigFactory.load().getConfig("client"))
  val serverPath = ActorPath.fromString("akka.tcp://server@127.0.0.1:2551/user/odbService")
  val server = sys.actorSelection(serverPath)

  server ! Activate
  server ! ListDatabases
  server ! Shutdown
  
}

