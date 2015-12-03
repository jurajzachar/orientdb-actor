package com.blueskiron.orientdb.embedded.api

/**
 * @author juri
 *
 */
object EmbeddedOrientDb {

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
  case object ListDatabases
}