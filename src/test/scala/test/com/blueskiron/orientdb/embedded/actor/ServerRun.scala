package test.com.blueskiron.orientdb.embedded.actor

import akka.actor.ActorSystem
import com.blueskiron.orientdb.embedded.actor.ServerActor
import akka.actor.Props
import com.blueskiron.orientdb.embedded.api.EmbeddedService.Activate
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object ServerRun extends App {
  val sys = ActorSystem("server", ConfigFactory.load().getConfig("server"))
  private lazy val props = Props(classOf[ServerActor], getClass.getResourceAsStream("/config/orientdb-server-config.xml"), "odb-node1")
  private lazy val server = sys.actorOf(props, name = "odbService")
  server ! Activate
}
