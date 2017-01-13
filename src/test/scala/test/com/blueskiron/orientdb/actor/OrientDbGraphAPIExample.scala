package test.com.blueskiron.orientdb.actor

import com.orientechnologies.orient.client.remote.OServerAdmin
import com.orientechnologies.orient.core.metadata.schema.OType
import com.orientechnologies.orient.core.sql.OCommandSQL
import com.tinkerpop.blueprints.Direction
import com.tinkerpop.blueprints.Edge
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientDynaElementIterable
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType
import com.tinkerpop.blueprints.impls.orient.OrientElementIterable
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import com.tinkerpop.blueprints.impls.orient.OrientVertexType
import scala.collection.JavaConverters._

object OserverAdminExample extends App {
  val serverAdmin = new OServerAdmin("remote:localhost").connect("root", "ch4ng3m3!")
  serverAdmin.createDatabase("digital-sympathy", "graph", "plocal")
}

object CommonGraph {
  val WorkEdgeLabel = "Work"
  // opens the DB (if not existing, it will create it)
  val uri: String = "remote:localhost/digital-sympathy"
  //val uri: String = "plocal:target/databases/ScalaSample"
  val factory: OrientGraphFactory = new OrientGraphFactory(uri)
  val graph: OrientGraph = factory.getTx()
}

object OrientDbGremlinExample extends App {
  import CommonGraph._
  //new GremlinPipeline(graph.getVertex(1)).out(WorkEdgeLabel).property("name").filter(new PipeFunction[String, Boolean]() {
  //  def compute(arg: String): Boolean = arg.startsWith("J")
  //})
}

object OrientDbGraphAPIExample extends App {

  import CommonGraph._

  try {

    // if the database does not contain the classes we need (it was just created),
    // then adds them
    if (graph.getVertexType("Person") == null) {

      // we now extend the Vertex class for Person and Company
      val person: OrientVertexType = graph.createVertexType("Person")
      person.createProperty("firstName", OType.STRING)
      person.createProperty("lastName", OType.STRING)

      val company: OrientVertexType = graph.createVertexType("Company")
      company.createProperty("name", OType.STRING)
      company.createProperty("revenue", OType.LONG)

      val project: OrientVertexType = graph.createVertexType("Project")
      project.createProperty("name", OType.STRING)

      // we now extend the Edge class for a "Work" relationship
      // between Person and Company
      val work: OrientEdgeType = graph.createEdgeType(WorkEdgeLabel)
      work.createProperty("startDate", OType.DATE)
      work.createProperty("endDate", OType.DATE)
      work.createProperty("projects", OType.LINKSET)
    } else {

      println("Cleaning up the DB since it was already created in a preceding run..")
      graph.command(new OCommandSQL("DELETE VERTEX V")).execute()
      graph.command(new OCommandSQL("DELETE EDGE E")).execute()
    }

    // adds some people
    val johnDoe: Vertex = graph.addVertex("class:Person", "firstName", "John", "lastName", "Doe")
    val johnSmith: Vertex = graph.addVertex("class:Person", "firstName", "John", "lastName", "Smith")
    val janeDoe: Vertex = graph.addVertex("class:Person", "firstName", "Jane", "lastName", "Doe")

    // creates a Company
    val acme: Vertex = graph.addVertex("class:Company", "name", "ACME", "revenue", "10000000")

    // creates a couple of projects
    val acmeGlue: Vertex = graph.addVertex("class:Project", "name", "ACME Glue")
    val acmeRocket: Vertex = graph.addVertex("class:Project", "name", "ACME Rocket")

    // creates edge JohnDoe worked for ACME
    val johnDoeAcme: Edge = graph.addEdge(null, johnDoe, acme, WorkEdgeLabel)
    johnDoeAcme.setProperty("startDate", "2010-01-01")
    johnDoeAcme.setProperty("endDate", "2013-04-21")
    johnDoeAcme.setProperty("projects", Set(acmeGlue, acmeRocket).asJava)

    // another way to create an edge, starting from the source vertex
    val johnSmithAcme: Edge = johnSmith.addEdge(WorkEdgeLabel, acme)
    johnSmithAcme.setProperty("startDate", "2009-01-01")
    graph.commit()

    // prints all the people who works/worked for ACME
    val sql = s"SELECT expand(in('${WorkEdgeLabel}')) FROM Company WHERE name='ACME'"
    println(s"Querying with '$sql'")
    println("Vertices: " + graph.countVertices())
    println("Edges: " + graph.countEdges())
    val res: OrientDynaElementIterable = graph
      .command(new OCommandSQL(sql))
      .execute()
    println("ACME people:")
    res.asScala.foreach { v =>
      println("Processing: " + v)

      // gets the person
      val person = v.asInstanceOf[OrientVertex]

      // gets the "Work" edge
      val workEdgeIterator = person.getEdges(Direction.OUT, WorkEdgeLabel).iterator()
      val edge = workEdgeIterator.next()

      // and retrieves info to print
      val status = if (edge.getProperty("endDate") != null) "retired" else "active"
      val projects = if (edge.getProperty("projects") != null)
        edge.getProperty("projects").asInstanceOf[OrientElementIterable[Vertex]].asScala.map(v => v.getProperty[String]("name")).mkString(", ") else "Any"

      println(s"Name: ${person.getProperty("lastName")}, ${person.getProperty("firstName")} [${status}]. Worked on: ${projects}.")
    }
  } catch {
    case (e: Exception) => println("ERROR: " + e)
  } finally {
    graph.shutdown()
  }
}