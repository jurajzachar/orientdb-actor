package test.com.blueskiron.orientdb.embedded.actor

import scala.concurrent.duration._
import scala.language.postfixOps
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.WordSpecLike
import org.slf4j.LoggerFactory
import com.blueskiron.orientdb.embedded.actor.ServerActor
import com.blueskiron.orientdb.embedded.api.EmbeddedService._
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.DefaultTimeout
import akka.testkit.ImplicitSender
import akka.testkit.TestKit
import scala.io.Source

class EmbeddedServiceSpec(testSystem: ActorSystem)
    extends TestKit(testSystem)
    with DefaultTimeout with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("EmbeddedSpec"))
  
  private val log = LoggerFactory.getLogger(getClass)
  
  private val defaultTimeout = 10 seconds
  lazy val props = Props(classOf[ServerActor], getClass.getResourceAsStream("/config/orientdb-server-config.xml"), "test-node")
  lazy val orientDbServiceRef = testSystem.actorOf(props, name = "orientDbService")
  
  override def beforeAll {
      log.info("actor created: ", orientDbServiceRef)
  }

  override def afterAll {
    orientDbServiceRef ! Shutdown
    TestKit.shutdownActorSystem(testSystem)
  }
  
  "EmbeddedService" must {
    "be active after 'Activate' message is received" in {
       orientDbServiceRef ! Activate
       expectMsgPF(defaultTimeout) {
         case false => fail("failed to activate")
         case true => //ignore
       }
    }
  }

}