[![Build Status](https://travis-ci.org/jurajzachar/orientdb-embedded.svg?branch=master)](https://travis-ci.org/jurajzachar/orientdb-embedded)

Minimalistic AKKA actor-controlled [Embedded OrientDB Server](http://orientdb.com/docs/2.1/Embedded-Server.html) 
===========================================================

> See project/Build.scala for OrientDb version used.

1. Create application.conf
--------------------------

	orientdb-embedded { 
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

3. Query server
---------------
	
	server ! IsActive //sends back boolean
	val databases = Await.result(server ? ListDatabases, timeout.duration).asInstanceOf[Map[String, String]]
  	
TO-DO
-----

* support for other queries
* expose info for jmx beans
  	
