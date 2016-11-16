package com.blueskiron.orientdb.embedded.actor

import com.blueskiron.orientdb.embedded.api.EmbeddedOrientDb.IsActive
import com.blueskiron.orientdb.embedded.api.EmbeddedOrientDb.ListDatabases
import com.blueskiron.orientdb.embedded.api.EmbeddedOrientDb.Shutdown
import com.orientechnologies.orient.server.OServerMain
import com.typesafe.config.Config

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.actorRef2Scala
import com.blueskiron.orientdb.embedded.api.EmbeddedOrientDb._
import scala.collection.JavaConverters._

/**
 * Heavy actor, use sparsely, i.e. one instance per VM
 * @author jza
 *
 * since Oct 17, 2016
 * Copyright (c) 2016. Celum GmbH. All rights reserved.
 */
class ServerActor(config: Config) extends Actor with ActorLogging {

  val configPath = config.getString(serverConfigKey)
  val ios = getClass.getResourceAsStream(configPath)

  val orientDbHome = config.getString(homeKey)
  val orientDbNodeName = config.getString(nodeNameKey)
  val orientDbRootPassword = config.getString(rootPasswordKey)

  //update ENV vars, needed by OrientdbServer
  System.setProperty("ORIENTDB_HOME", orientDbHome)
  System.setProperty("ORIENTDB_NODE_NAME", orientDbNodeName)
  //System.setProperty("ORIENTDB_ROOT_PASSWORD", orientDbRootPassword)

  private val server = OServerMain.create()
    .startup(ios)

  def receive = {
    case StartUp => {
      log.info("Starting up {} embedded instance...", orientDbNodeName)
      server.activate()
    }
    case IsActive      => sender ! server.isActive()
    case ListDatabases => sender ! ListDatabases(server.getAvailableStorageNames.asScala.toMap)
    case Shutdown => {
      log.info("Shutting down '{}' embedded instance...", orientDbNodeName)
      sender ! server.shutdown()
      //destroys itself
      context.stop(self)
    }
  }

}