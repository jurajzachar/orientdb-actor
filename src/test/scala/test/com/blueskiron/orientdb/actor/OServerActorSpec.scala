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

  "Embedded OrientDB server" must {
    "be 'active' when " + OServerActor.StartUp + " message is sent to it" in {
      orientDbServiceRef ! OServerActor.StartUp
      orientDbServiceRef ! OServerActor.IsActive
      expectMsgPF(defaultTimeout) {
        case false => fail("failed to bring up the server")
        case true => log.info("Received confirmation OrientDB server is active!")
      }
    }
  }

  "Embedded OrientDB server" must {
    "be 'shut down' when " + OServerActor.Shutdown + " message is sent to it" in {
      orientDbServiceRef ! OServerActor.StartUp
      orientDbServiceRef ! OServerActor.Shutdown
      expectMsgPF(defaultTimeout) {
        case false => fail("failed to bring up the server")
        case true => log.info("Received confirmation OrientDB server is active!")
      }
      orientDbServiceRef ! OServerActor.IsActive
      expectMsgPF(defaultTimeout) {
        case false => log.info("Received confirmation OrientDB server is active!")
        case true => fail("failed to bring up the server")
      }
    }
  }

  "Embedded OrientDB server" must {
    "list all available databases when " + OServerActor.ListDatabases + " message is sent to it" in {
      orientDbServiceRef ! OServerActor.ListDatabases
      expectMsgPF(defaultTimeout) {
        case response => log.info("Received {} response to message {}", response, OServerActor.ListDatabases)
      }
    }
  }

}
