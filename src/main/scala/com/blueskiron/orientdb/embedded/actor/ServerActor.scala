package com.blueskiron.orientdb.embedded.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import com.orientechnologies.orient.server.OServerMain
import com.blueskiron.orientdb.embedded.api.EmbeddedService._
import com.orientechnologies.orient.server.OServer
import java.io.InputStream

class ServerActor(config: InputStream, nodeName: String) extends Actor with ActorLogging {
  
  private object EmbeddedServer {
    System.setProperty("ORIENTDB_HOME", ".")
    System.setProperty("ORIENTDB_NODE_NAME", nodeName)
    val server = OServerMain.create()
    .startup(config)
  }
  
  lazy val server = EmbeddedServer.server
  
  private def activate = {
    if (!server.isActive()) server.activate()
    server.isActive()
  }

  def receive = {
    case Activate => sender ! activate
    case Shutdown => sender ! server.shutdown()
  }
}