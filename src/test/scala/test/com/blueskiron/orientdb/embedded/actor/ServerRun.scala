package test.com.blueskiron.orientdb.embedded.actor

import com.blueskiron.orientdb.embedded.actor.ServerActor
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props

object ServerRun extends App {
  
  val config  = ConfigFactory.load
  val sys = ActorSystem("server", config.getConfig("server"))
  private lazy val props = Props(classOf[ServerActor], config.getConfig("orientdb-embedded"))
  private lazy val server = sys.actorOf(props, name = "odbService")

}
