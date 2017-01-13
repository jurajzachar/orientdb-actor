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
import akka.actor.ActorRef

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

}

/**
 * Heavy actor, use sparsely, i.e. one instance per VM
 *
 * since Oct 17, 2016
 */
class OServerActor(config: Config) extends Actor with ActorLogging {
  import OServerActor._
  import OServerActorMessage._

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

  override def preStart { boot(context.self) }

  override def postStop { destroy(context.self) }

  private def destroy(requestor: ActorRef) {
    log.info(s"Shutting down OServer $orientDbNodeName")
    replyAndPublishEvent(requestor, Shutdown(ServerStatus(orientDbNodeName, server.shutdown())))
  }

  private def boot(requestor: ActorRef) = {
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
        val isActive = server.startup(config).activate.isActive()
        replyAndPublishEvent(requestor, StartUp(ServerStatus(orientDbNodeName, isActive), None))
      }
      case Failure(reason) => {
        log.error("Failed to start OrientDB server ", reason)
        replyAndPublishEvent(requestor, StartUp(ServerStatus(orientDbNodeName, server.isActive()), Some(reason)))
        context.unbecome()
      }
    }
  }

  private def replyAndPublishEvent(requestor: ActorRef, msg: OServerActorMessage) {
    requestor ! msg
    context.system.eventStream.publish(msg)
  }

  def receive = {
    case StartUp => {
      if (!server.isActive()) {
        boot(sender)
      } else {
        log.warning("{} already active", orientDbNodeName)
        sender ! StartUp(ServerStatus(orientDbNodeName, server.isActive()), None)
      }
    }
    case Shutdown => {
      if (!server.isActive()) {
        log.warning("{} already inactive", orientDbNodeName)
        sender ! StartUp(ServerStatus(orientDbNodeName, server.isActive()), None)
      } else {
        destroy(sender)
      }
    }
    case ServerStatus                    => sender ! ServerStatus(orientDbNodeName, server.isActive())
    case ListDatabases                   => sender ! ListDatabases(orientDbNodeName, server.getAvailableStorageNames.asScala.toMap)

    //ignore the startup and shutdown messages
    case StartUp(status, maybeException) => //ignore...
    case Shutdown(status)                => //ignore...
    //should not hit this block
    case msg: Any => {
      log.error(s"Unexpected messaged ignored: $msg")
    }
  }

}