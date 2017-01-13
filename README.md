[![Build Status](https://travis-ci.org/jurajzachar/orientdb-actor.svg?branch=master)](https://travis-ci.org/jurajzachar/orientdb-actor)

AKKA actor-controlled [Embedded OrientDB Server](http://orientdb.com/docs/2.1/Embedded-Server.html) 
===========================================================

> See project/Dependencies.scala for OrientDb version used.

1. Create application.conf
--------------------------

	orientdb-actor { 
		orient-db-config = "/path/to/config/orientdb-server-config.xml"
		orient-db-home = "/path/to/orientdb-home/with/databases"
		orient-db-node-name = odb-node1
	}

2. Deploy OrientDb Server Actor
-------------------------------

	val config  = ConfigFactory.load
	val sys = ActorSystem("server", config.getConfig("server"))
	private lazy val props = Props(classOf[ServerActor], config.getConfig("orientdb-embedded"))
	private lazy val server = sys.actorOf(props, name = "odbService")

3. Send messages to the actor
-----------------------------

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
	server ! IsActive //sends back boolean
	val databases = Await.result(server ? ListDatabases, timeout.duration).asInstanceOf[Map[String, String]]
  	
TO-DO
-----

* support for other queries/admin operations
  	
