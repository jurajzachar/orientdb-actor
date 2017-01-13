package test.com.blueskiron.orientdb.actor

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.WordSpecLike
import org.slf4j.LoggerFactory

import com.blueskiron.orientdb.actor._
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.testkit.DefaultTimeout
import akka.testkit.ImplicitSender
import akka.testkit.TestKit

class OServerActorSpec(testSystem: ActorSystem)
    extends TestKit(testSystem)
    with DefaultTimeout with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("OserverActorSpec"))

  private val log = LoggerFactory.getLogger(getClass)

  private val defaultTimeout = 25 seconds
  val config = ConfigFactory.load()
  lazy val props = Props(classOf[OServerActor], config.getConfig("orientdb-actor"))
  lazy val orientDbServiceRef = testSystem.actorOf(props, name = OServerActor.defaultOServerActorname(config))

  override def beforeAll {
    log.info(s"Actor created: $orientDbServiceRef.path")
  }

  override def afterAll {
    TestKit.shutdownActorSystem(testSystem)
  }

  "OrientDB OServerActor" must {
    "be 'active' when " + OServerActorMessage.StartUp + " message is sent to it" in {
      orientDbServiceRef ! OServerActorMessage.StartUp
      expectMsgPF(defaultTimeout) {
        case OServerActorMessage.StartUp(serverStatus, None) if serverStatus.isActive => log.info(s"Received startup confirmation '$serverStatus'!")
        case OServerActorMessage.StartUp(serverStatus, Some(startProblem)) => fail(startProblem)
        case _ => fail("failed to bring up the server")
      }
    }

    "be 'shut down' when " + OServerActorMessage.Shutdown + " message is sent to it" in {
      orientDbServiceRef ! OServerActorMessage.Shutdown
      expectMsgPF(defaultTimeout) {
        case OServerActorMessage.Shutdown(serverStatus) if serverStatus.isActive => log.info(s"Received shutdown confirmation '$serverStatus'!")
        case msg: Any => fail(s"failed to bring down the server --> $msg")
      }
      orientDbServiceRef ! OServerActorMessage.ServerStatus
      expectMsgPF(defaultTimeout) {
        case OServerActorMessage.ServerStatus(orientDbNodeName, isActive) if !isActive => log.info(s"Received confirmation '$orientDbNodeName' server isActive: '$isActive'!")
        case _ => fail("failed to bring down the server")
      }
    }

    "list all available databases when " + OServerActorMessage.ListDatabases + " message is sent to it" in {
      orientDbServiceRef ! OServerActorMessage.ListDatabases
      expectMsgPF(defaultTimeout) {
        case OServerActorMessage.ListDatabases(orientDbNodeName, map) => log.info(s"Received the folowing databases info for '$orientDbNodeName': $map")
        case _ => fail("failed to query the server")
      }
    }
  }

}
