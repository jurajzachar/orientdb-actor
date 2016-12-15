package test.com.blueskiron.orientdb.actor

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.WordSpecLike
import org.slf4j.LoggerFactory

import com.blueskiron.orientdb.actor._
import com.blueskiron.orientdb.actor.OServerActorMessages._
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.testkit.DefaultTimeout
import akka.testkit.ImplicitSender
import akka.testkit.TestKit

class OserverActorSpec(testSystem: ActorSystem)
    extends TestKit(testSystem)
    with DefaultTimeout with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("OserverActorSpec"))

  private val log = LoggerFactory.getLogger(getClass)

  private val defaultTimeout = 25 seconds
  lazy val props = Props(classOf[OServerActor], ConfigFactory.load().getConfig("orientdb-actor"))
  lazy val orientDbServiceRef = testSystem.actorOf(props, name = "OrientDbService")

  override def beforeAll {
    log.info("Actor created: ", orientDbServiceRef)
  }

  override def afterAll {
    TestKit.shutdownActorSystem(testSystem)
  }

  "Embedded OrientDB server" must {
    "be 'active' when " + StartUp + " message is sent to it" in {
      orientDbServiceRef ! StartUp
      orientDbServiceRef ! IsActive
      expectMsgPF(defaultTimeout) {
        case false => fail("failed to bring up the server")
        case true => log.info("Received confirmation OrientDB server is active!")
      }
    }
  }

  "Embedded OrientDB server" must {
    "be 'shut down' when " + Shutdown + " message is sent to it" in {
      orientDbServiceRef ! StartUp
      orientDbServiceRef ! Shutdown
      expectMsgPF(defaultTimeout) {
        case false => fail("failed to bring up the server")
        case true => log.info("Received confirmation OrientDB server is active!")
      }
      orientDbServiceRef ! IsActive
      expectMsgPF(defaultTimeout) {
        case false => log.info("Received confirmation OrientDB server is active!")
        case true => fail("failed to bring up the server")
      }
    }
  }

  "Embedded OrientDB server" must {
    "list all available databases when " + ListDatabases + " message is sent to it" in {
      orientDbServiceRef ! ListDatabases
      expectMsgPF(defaultTimeout) {
        case response => log.info("Received {} response to message {}", response, ListDatabases)
      }
    }
  }

}
