package test.com.blueskiron.orientdb.embedded.actor

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import com.blueskiron.orientdb.embedded.api.EmbeddedOrientDb._
import com.typesafe.config.ConfigFactory

import akka.actor.ActorPath
import akka.pattern.ask
import akka.actor.ActorSelection.toScala
import akka.actor.ActorSystem
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global

object ClientRunExample extends App {

  val sys = ActorSystem("client", ConfigFactory.load().getConfig("client"))
  val serverPath = ActorPath.fromString("akka.tcp://example-system@127.0.0.1:2551/user/OrientDbServerActor")
  val server = sys.actorSelection(serverPath)
  
  implicit val timeout = Timeout(10 seconds)
  
  //tell server to startUp
  server ! StartUp
  
  val isActive = Await.result(server ? IsActive,  timeout.duration).asInstanceOf[Boolean]
  println("Embedded orientDb is active: " + isActive)
  
  val databases = Await.result(server ? ListDatabases, timeout.duration)
  println("List databases: " + databases)
  
  val isShutDown = Await.result(server ? Shutdown, timeout.duration).asInstanceOf[Boolean]
  println("Server is shutdown")
 
  System.exit(0)
}

