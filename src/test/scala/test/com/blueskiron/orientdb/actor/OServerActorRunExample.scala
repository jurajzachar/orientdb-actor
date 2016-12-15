package test.com.blueskiron.orientdb.actor

import com.blueskiron.orientdb.actor._
import com.blueskiron.orientdb.actor.OServerActorMessages._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor
import akka.actor.Terminated
import scala.concurrent.Promise
import scala.concurrent.ExecutionContext.Implicits.global

object OServerActorRunExample extends App {

  val config = ConfigFactory.load
  val sys = ActorSystem("example-system", config.getConfig("server"))
  val props = Props(classOf[OServerActor], config.getConfig("orientdb-actor"))
  val server = sys.actorOf(props, name = "OServerActor")

  val serverWatch = sys.actorOf(Props(classOf[OServerActorWatch]), name = "OServerActorWatch")

  val shutDownHook = Promise[Unit]()
  shutDownHook.future.onComplete { case _ => sys.terminate() }

  private class OServerActorWatch extends Actor {
    context.watch(server)
    def receive = {
      case msg: Terminated => {
        println("ServerWatch received: " + msg)
        shutDownHook.success(Unit)
      }
    }
  }

}
