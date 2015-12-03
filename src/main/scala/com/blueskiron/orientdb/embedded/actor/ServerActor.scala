package com.blueskiron.orientdb.embedded.actor

import com.blueskiron.orientdb.embedded.api.EmbeddedOrientDb.IsActive
import com.blueskiron.orientdb.embedded.api.EmbeddedOrientDb.ListDatabases
import com.blueskiron.orientdb.embedded.api.EmbeddedOrientDb.Shutdown
import com.orientechnologies.orient.server.OServerMain
import com.typesafe.config.Config

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.actorRef2Scala

class ServerActor(config: Config) extends Actor with ActorLogging {

  val configPath = config.getString("orient-db-config")
  val ios = getClass.getResourceAsStream(configPath)
  val orientDbHome = config.getString("orient-db-home")
  val orientDbNodeName = config.getString("orient-db-node-name")
  System.setProperty("ORIENTDB_HOME", orientDbHome)
  System.setProperty("ORIENTDB_NODE_NAME", orientDbNodeName)
  private val server = OServerMain.create()
    .startup(ios)
    .activate()

  def receive = {
    case IsActive      => sender ! server.isActive()
    case ListDatabases => sender ! server.getAvailableStorageNames
    case Shutdown => {
      sender ! server.shutdown()
      context.stop(self)
    }
  }

}