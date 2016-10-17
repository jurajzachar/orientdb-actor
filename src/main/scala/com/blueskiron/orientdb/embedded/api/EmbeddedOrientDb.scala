package com.blueskiron.orientdb.embedded.api

/**
 * @author juri
 *
 */
object EmbeddedOrientDb {
  
  private val nameSpace = "orientdb"
  val serverConfigKey = nameSpace + "-" + "server-config";
  val homeKey = nameSpace + "-" + "home";
  val nodeNameKey = nameSpace + "-" + "node-name";
  val rootPasswordKey = nameSpace + "-" + "root-password";

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