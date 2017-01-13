package com.blueskiron.orientdb.actor

/**
 * @author juri
 *
 */
object OServerActorMessage {

  /**
   * start up embedded server
   *
   */
  case object StartUp
  case class StartUp(status: ServerStatus, failure: Option[Throwable]) extends OServerActorMessage(status.orientDbNodeName)

  /**
   * check if the embedded server is active
   *
   */
  case object ServerStatus
  case class ServerStatus(override val orientDbNodeName: String, isActive: Boolean) extends OServerActorMessage(orientDbNodeName)

  /**
   * shutdowns embedded server
   *
   */
  case object Shutdown
  case class Shutdown(status: ServerStatus) extends OServerActorMessage(status.orientDbNodeName)

  /**
   * lists available databases
   */
  case object ListDatabases
  case class ListDatabases(override val orientDbNodeName: String, result: Map[String, String]) extends OServerActorMessage(orientDbNodeName)
}

abstract class OServerActorMessage(val orientDbNodeName: String)
