package com.blueskiron.orientdb.actor

import scala.collection.JavaConverters.mapAsScalaMapConverter

import com.orientechnologies.orient.server.OServer
import com.orientechnologies.orient.server.OServerMain
import com.typesafe.config.Config

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.actorRef2Scala
import java.io.File
import java.io.FileNotFoundException
import scala.io.Source
import scala.io.Codec
import scala.util.Try
import scala.util.Success
import scala.util.Failure

/**
 * @author juri
 *
 */
object OServerActor {
  private val nameSpace = "orientdb"
  val rootConfigKey = nameSpace + "-actor"
  val serverConfigKey = nameSpace + "-" + "server-config";
  val homeKey = nameSpace + "-" + "home";
  val nodeNameKey = nameSpace + "-" + "node-name";
  val distributedKey = "distributed" //ambiguous, OrientDB breaks naming convention here
  val rootPasswordKey = nameSpace + "-" + "root-password";

  def defaultOServerActorname(config: Config) =
    config.getConfig(rootConfigKey).getString(OServerActor.nodeNameKey)

  /**
   * start up embedded server
   *
   */
  case object StartUp

  /**
   * check is the embedded server is active
   *
   */
  case object IsActive

  /**
   * shutdowns embedded server
   *
   */
  case object Shutdown

  /**
   * lists available databases
   */
  case class ListDatabases(result: Map[String, String])
}

/**
 * Heavy actor, use sparsely, i.e. one instance per VM
 *
 * since Oct 17, 2016
 */
class OServerActor(config: Config) extends Actor with ActorLogging {
  import OServerActor._

  private val server: OServer = OServerMain.create

  val orientDbHome = config.getString(homeKey)
  val orientDbNodeName = config.getString(nodeNameKey)
  val distributed = config.getBoolean(distributedKey)

  val configFile: Try[String] = {
    try {
      val configPath = config.getString(serverConfigKey)
      Success(Source.fromFile(configPath).getLines.mkString)
    } catch {
      case ex: Exception => {
        Failure(ex)
      }
    }
  }

  override def preStart {
    //update ENV vars, needed by OrientdbServer
    log.info(s"Setting ORIENTDB_HOME to $orientDbHome")
    System.setProperty("ORIENTDB_HOME", orientDbHome)
    log.info(s"Setting ORIENTDB_NODE_NAME to $orientDbNodeName")
    System.setProperty("ORIENTDB_NODE_NAME", orientDbNodeName)
    log.info(s"Setting 'distributed' to $distributed")
    System.setProperty("distributed", String.valueOf(distributed))
    configFile match {
      case Success(config) => {
        log.info("Starting up OrientDB '{}' as embedded instance...", orientDbNodeName)
        server.startup(config).activate
      }
      case Failure(t) => {
        log.error("Failed to start OrientDB server ", t)
        context.unbecome()
      }
    }
  }

  //shutdown OServer when this actor gets destroyed
  override def postStop {
    log.info("Shutting down OServer... {}", server.shutdown())
  }

  def receive = {
    case StartUp => {
      if (!server.isActive()) {
        preStart
      } else {
        log.warning("{} already active", orientDbNodeName)
      }
    }
    case IsActive => sender ! server.isActive()
    case ListDatabases => sender ! ListDatabases(server.getAvailableStorageNames.asScala.toMap)
    case Shutdown => {
      log.info("Shutting down '{}' embedded instance...", orientDbNodeName)
      sender ! server.shutdown()
    }
    case msg: Any => {
      log.warning(s"Unexpected messaged ignored: $msg")
    }
  }

}