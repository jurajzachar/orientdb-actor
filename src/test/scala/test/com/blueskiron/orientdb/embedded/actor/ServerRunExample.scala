package test.com.blueskiron.orientdb.embedded.actor

import com.blueskiron.orientdb.embedded.actor.ServerActor
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor
import akka.actor.Terminated
import scala.concurrent.Promise
import scala.concurrent.ExecutionContext.Implicits.global

object ServerRunExample extends App {
  
  val config  = ConfigFactory.load
  val sys = ActorSystem("example-system", config.getConfig("server"))
  val props = Props(classOf[ServerActor], config.getConfig("orientdb-embedded"))
  val server = sys.actorOf(props, name = "OrientDbServerActor")
  
  val serverWatch = sys.actorOf(Props(classOf[ServerWatch]), name="OrientDbServerActorWatch")
  
  val shutDownHook = Promise[Unit]()
  shutDownHook.future.onComplete { case _ => sys.terminate() }
  
  private class ServerWatch extends Actor {
    context.watch(server)
    def receive = {
      case msg: Terminated => {
        println("ServerWatch received: " + msg)
        shutDownHook.success(Unit)
      }
    }
  }
  
}
