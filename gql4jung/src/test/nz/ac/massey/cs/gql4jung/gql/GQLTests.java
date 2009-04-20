package test.nz.ac.massey.cs.gql4jung.gql;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.gql4jung.Constraint;
import nz.ac.massey.cs.gql4jung.DefaultMotif;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.LinkConstraint;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.PropertyConstraint;
import nz.ac.massey.cs.gql4jung.constraints.EdgeConstraint;
import nz.ac.massey.cs.gql4jung.constraints.PathConstraint;
import nz.ac.massey.cs.gql4jung.impl.Bindings;
import nz.ac.massey.cs.gql4jung.impl.ConstraintSchedulerImpl;
import nz.ac.massey.cs.gql4jung.impl.GQLImpl;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;
import nz.ac.massey.cs.utils.odem2graphml.Odem2GraphML;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.UserData;

public class GQLTests {
	static {
		// only necessary when running inside massey
		System.getProperties().put( "proxySet", "true" );
		System.getProperties().put( "proxyHost", "tur-cache" );
		System.getProperties().put( "proxyPort", "8080" );
	}
	

	@Before
	public void init() {
		
	}
	@After
	public void release() throws Exception {
		
	}
	// see also http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/io/GraphMLFile.html
	private Graph readJungGraphFromGraphML(String graphSource) throws Exception {	
		GraphMLFile input = new GraphMLFile();
		Reader reader = new FileReader(graphSource);
		Graph g = new DirectedSparseGraph();
		g =	input.load(reader);
		reader.close();
		return g;
	}
	private Graph readJungGraphFromGraphML(Reader reader) throws Exception {
		GraphMLFile input = new GraphMLFile();
		Graph g = input.load(reader);
		reader.close();
		return g;
	}
	private Graph readJungGraphFromODEM(String odemSource) throws Exception {
		Odem2GraphML converter = new Odem2GraphML();
		File odem = new File(odemSource);
		Reader odemReader = new FileReader(odem);
		// jens: we try to do this in memory, if this does not scale,
		// change to filewriter using tmp files
		StringWriter writer = new StringWriter();
		converter.convert(odemReader, writer);
		odemReader.close();
		writer.close();
		String graphML = writer.getBuffer().toString();
		StringReader graphmlReader = new StringReader(graphML);
		return readJungGraphFromGraphML(graphmlReader);
	}
	private Motif readMotif(String motifSource) throws Exception {
		// refer to your own MotifParser here (unmarshal)
		// use XMLMotifReader
		XMLMotifReader r = new XMLMotifReader();
		DefaultMotif q = (DefaultMotif) r.read(new FileInputStream (motifSource));
		return q;
		
	}

	//to check bindings and selectNext method in Constraint scheduler
	@Test
	public void test1()throws Exception{
		Graph g = readJungGraphFromGraphML("test_examples/abstraction.graphml");
		Vertex v2 = null, v0=null, v1=null;
		Vertex newtest = getVertexById(g, "Animal");
		newtest.addUserDatum("testkey", "testData", UserData.SHARED);
		Graph gr = new SparseGraph();
		newtest.copy(gr);
		Vertex copiedVertex = getVertexById(gr, "Animal");
		System.out.println(copiedVertex.getUserDatum("testkey"));
		newtest.setUserDatum("testkey", "UpdatedData", UserData.SHARED);
		System.out.println(copiedVertex.getUserDatum("testkey"));
		System.out.println(newtest.getUserDatum("testkey"));
		copiedVertex.setUserDatum("testkey", "copiedData", UserData.SHARED);
		System.out.println(copiedVertex.getUserDatum("testkey"));

		for (Object v:g.getVertices()) {
			if (((Vertex)v).getUserDatum("name").equals("MyApplication")) 
				v2=(Vertex)v;
			else if (((Vertex)v).getUserDatum("name").equals("Animal")) 
				v0=(Vertex)v;
			else if (((Vertex)v).getUserDatum("name").equals("Horse")) 
				v1=(Vertex)v;
		}
		DefaultMotif q = (DefaultMotif) readMotif("xml/abstraction_coupling.xml");
		ConstraintSchedulerImpl cs = new ConstraintSchedulerImpl();
		List<Constraint> constraints = cs.getConstraints(q);
		//List<Constraint> sortedConstraints = cs.prepare(g, constraints);
		Bindings binding = new Bindings();
		binding.bind(q.getRoles().get(1),v0);
		binding.gotoChildLevel();
		binding.bind(q.getRoles().get(2),v1);
		binding.gotoChildLevel();
		binding.bind(q.getRoles().get(0),v2);
		Constraint c = cs.selectNext(g, constraints, binding);
		if(c instanceof PropertyConstraint){
				PropertyConstraint pc = (PropertyConstraint)c;
				assertEquals(pc.getOwner(),"client");		
		}
		else
			fail("Sorry");
	}
	// Testcase 1.1: Execute the above test case for both interfaces and abstract classes. 
	// Testcase 1.2: Test for packages instead of classes. 
	// Testcase 1.3: Test for inheritence hierarchies i.e. when abstrac class is extended to multiple levels
	
	
	
	// Testcase 2.1: Test for circular dependency between packages
	// Testcase 2.2: Test for circular dependency between more than 3 classes/packages
	// Testcase 2.3: Test for dependency path (uses relationship) of length 3 or more to 
	//               detect the ripple effect in the software.
		
	

	// Testcase 3.1: Test for different types of Database files e.g. MySQL, PostgreSQL, Access
	// Testcase 3.2: Test for swing & awt classes, used in a class 
	
	
	// Testcase 4: Test for multiple clusters in one package
	// Testcase 4.1: Test for no clusters in one package
	
		
	//No decoupling through abstraction
	@Test
	public void test2() throws Exception {
		Graph g = readJungGraphFromGraphML("test_examples/abstraction.graphml");
		//Graph g = readJungGraphFromGraphML("data/activation.jar.graphml");
		Motif q = (DefaultMotif) readMotif("xml/abstraction_coupling.xml");
		GQL gql = new GQLImpl();
		ResultCollector listener = new ResultCollector();
		gql.query(g, q, listener);
		// analyse results
		assertEquals(listener.getInstances().size(),1);
		for (MotifInstance result:listener.getInstances()) {
			assertEquals(result.getVertex("client"),this.getVertexById(g,"MyApplication"));
			assertEquals(result.getVertex("service"),this.getVertexById(g,"Animal"));
			assertEquals(result.getVertex("service_impl"),this.getVertexById(g,"Horse"));
			Edge p1 = (Edge)result.getLink(getConstraint(q,"client","service"));
			assertTrue(p1 == (this.getEdgeById(g,"edge-4")));
			Path p2 = (Path)result.getLink(getConstraint(q,"client","service_impl"));
			assertTrue(p2.getEdges().contains(this.getEdgeById(g,"edge-5")));
			Path p3 = (Path)result.getLink(getConstraint(q,"service_impl","service"));
			assertTrue(p3.getEdges().contains(this.getEdgeById(g,"edge-1")));
		}
	}
	//A variation in test2 to detect 2 patterns in the graph
	@Test
	public void test2_1() throws Exception {
		Graph g = readJungGraphFromGraphML("test_examples/abstraction1.graphml");
		Motif q = (DefaultMotif) readMotif("xml/abstraction_coupling.xml");
		GQL gql = new GQLImpl();
		ResultCollector listener = new ResultCollector();
		gql.query(g, q, listener);
		List<MotifInstance> result = listener.getInstances();
		// analyse 1st results
		assertEquals(listener.getInstances().size(),2);
		MotifInstance instance1 = result.get(1);
		assertEquals(instance1.getVertex("client"),this.getVertexById(g,"MyApplication"));
		assertEquals(instance1.getVertex("service"),this.getVertexById(g,"Animal"));
		assertEquals(instance1.getVertex("service_impl"),this.getVertexById(g,"Cow"));
		Path p1 = (Path)instance1.getLink(getConstraint(q,"client","service"));
		assertTrue(p1.getEdges().contains(this.getEdgeById(g,"edge-5")));
		Path p2 = (Path)instance1.getLink(getConstraint(q,"client","service_impl"));
		assertTrue(p2.getEdges().contains(this.getEdgeById(g,"edge-7")));
		Path p3 = (Path)instance1.getLink(getConstraint(q,"service_impl","service"));
		assertTrue(p3.getEdges().contains(this.getEdgeById(g,"edge-1")));
		//analyse 2nd result
		MotifInstance instance2 = result.get(0);
		assertEquals(instance2.getVertex("client"),this.getVertexById(g,"MyApplication"));
		assertEquals(instance2.getVertex("service"),this.getVertexById(g,"Animal"));
		assertEquals(instance2.getVertex("service_impl"),this.getVertexById(g,"Horse"));
		Path p4 = (Path)instance2.getLink(getConstraint(q,"client","service"));
		assertTrue(p4.getEdges().contains(this.getEdgeById(g,"edge-5")));
		Path p5 = (Path)instance2.getLink(getConstraint(q,"client","service_impl"));
		assertTrue(p5.getEdges().contains(this.getEdgeById(g,"edge-6")));
		Path p6 = (Path)instance2.getLink(getConstraint(q,"service_impl","service"));
		assertTrue(p6.getEdges().contains(this.getEdgeById(g,"edge-2")));
	}
	//A variation in test2 to detect 2 patterns in the graph by having multiple edges (Path)
	@Test
	public void test2_2() throws Exception {
		Graph g = readJungGraphFromGraphML("test_examples/abstraction2.graphml");
		Motif q = (DefaultMotif) readMotif("xml/abstraction_coupling.xml");
		GQL gql = new GQLImpl();
		ResultCollector listener = new ResultCollector();
		gql.query(g, q, listener);
		List<MotifInstance> result = listener.getInstances();
		// analyse 1st results
		assertEquals(listener.getInstances().size(),2);
		MotifInstance instance1 = result.get(0);
		assertEquals(instance1.getVertex("client"),this.getVertexById(g,"MyApplication"));
		assertEquals(instance1.getVertex("service"),this.getVertexById(g,"Animal"));
		assertEquals(instance1.getVertex("service_impl"),this.getVertexById(g,"Pony"));
		Path p1 = (Path)instance1.getLink(getConstraint(q,"client","service"));
		assertTrue(p1.getEdges().contains(this.getEdgeById(g,"edge-7")));
		Path p2 = (Path)instance1.getLink(getConstraint(q,"client","service_impl"));
		assertTrue(p2.getEdges().contains(this.getEdgeById(g,"edge-9")));
		Path p3 = (Path)instance1.getLink(getConstraint(q,"service_impl","service"));
		assertTrue(p3.getEdges().contains(this.getEdgeById(g,"edge-1")));
		//analyse 2nd result
		MotifInstance instance2 = result.get(1);
		assertEquals(instance2.getVertex("client"),this.getVertexById(g,"MyApplication"));
		assertEquals(instance2.getVertex("service"),this.getVertexById(g,"Animal"));
		assertEquals(instance2.getVertex("service_impl"),this.getVertexById(g,"Horse"));
		Path p4 = (Path)instance2.getLink(getConstraint(q,"client","service"));
		assertTrue(p4.getEdges().contains(this.getEdgeById(g,"edge-7")));
		Path p5 = (Path)instance2.getLink(getConstraint(q,"client","service_impl"));
		assertTrue(p5.getEdges().contains(this.getEdgeById(g,"edge-8")));
		Path p6 = (Path)instance2.getLink(getConstraint(q,"service_impl","service"));
		assertTrue(p6.getEdges().contains(this.getEdgeById(g,"edge-2")));
	}
	
	//A variation in test2 to analyse false positives. 
	@Test
	public void test2_3() throws Exception {
		Graph g = readJungGraphFromGraphML("test_examples/abstraction3.graphml");
		Motif q = (DefaultMotif) readMotif("xml/abstraction_coupling.xml");
		GQL gql = new GQLImpl();
		ResultCollector listener = new ResultCollector();
		gql.query(g, q, listener);
		List<MotifInstance> result = listener.getInstances();
		// analyse 1st results
		assertEquals(listener.getInstances().size(),2);
		MotifInstance instance1 = result.get(0);
		assertEquals(instance1.getVertex("client"),this.getVertexById(g,"MyApplication"));
		assertEquals(instance1.getVertex("service"),this.getVertexById(g,"Animal"));
		assertEquals(instance1.getVertex("service_impl"),this.getVertexById(g,"Pony"));
		Path p1 = (Path)instance1.getLink(getConstraint(q,"client","service"));
		assertTrue(p1.getEdges().contains(this.getEdgeById(g,"edge-7")));
		Path p2 = (Path)instance1.getLink(getConstraint(q,"client","service_impl"));
		assertTrue(p2.getEdges().contains(this.getEdgeById(g,"edge-10")));
		Path p3 = (Path)instance1.getLink(getConstraint(q,"service_impl","service"));
		assertTrue(p3.getEdges().contains(this.getEdgeById(g,"edge-1")));
		//analyse 2nd result
		MotifInstance instance2 = result.get(1);
		assertEquals(instance2.getVertex("client"),this.getVertexById(g,"MyApplication"));
		assertEquals(instance2.getVertex("service"),this.getVertexById(g,"Animal"));
		assertEquals(instance2.getVertex("service_impl"),this.getVertexById(g,"Horse"));
		Path p4 = (Path)instance2.getLink(getConstraint(q,"client","service"));
		assertTrue(p4.getEdges().contains(this.getEdgeById(g,"edge-7")));
		Path p5 = (Path)instance2.getLink(getConstraint(q,"client","service_impl"));
		assertTrue(p5.getEdges().contains(this.getEdgeById(g,"edge-8")));
		Path p6 = (Path)instance2.getLink(getConstraint(q,"service_impl","service"));
		assertTrue(p6.getEdges().contains(this.getEdgeById(g,"edge-2")));
	}
@Test
//	Testcase : circular dependency between classes and packages. 
	public void test3() throws Exception {
		Graph g = this.readJungGraphFromGraphML("test_examples/packageB/dependency.graphml");
		XMLMotifReader r = new XMLMotifReader();
		DefaultMotif q = (DefaultMotif) r.read(new FileInputStream ("xml/circular_dependency.xml"));
		GQL gql = new GQLImpl();
		ResultCollector rc = new ResultCollector();
		gql.query(g,q,rc);
		List<MotifInstance> results = rc.getInstances();
		assertEquals(1,results.size());
		MotifInstance instance1 = results.get(0);
		assertEquals("Class1",instance1.getVertex("class1").getUserDatum("name"));
		assertEquals("Class3",instance1.getVertex("class3").getUserDatum("name"));
		assertEquals("Class2",instance1.getVertex("class2").getUserDatum("name"));
	}
	@Test
	// Testcase : Test for a class depending on both UI layer and DB layer
	
	public void test4() throws Exception {
		Graph g = this.readJungGraphFromGraphML("test_examples/separation.graphml");
		XMLMotifReader r = new XMLMotifReader();
		DefaultMotif q = (DefaultMotif) r.read(new FileInputStream ("xml/db2ui_dependency.xml"));
		ResultCollector rc = new ResultCollector();
		GQL gql = new GQLImpl();
		gql.query(g,q,rc);
		List<MotifInstance> results = rc.getInstances();
		assertEquals(6,results.size());
		MotifInstance instance1 = results.get(0);
		assertEquals("CounterPanel",instance1.getVertex("uilayerclass").getUserDatum("name"));
		assertEquals("RunDB",instance1.getVertex("dblayerclass").getUserDatum("name"));
	}
	@Test
//	Testcase : two clusters in same package 
	public void test5() throws Exception {
		Graph g = this.readJungGraphFromGraphML("test_examples/clustering.graphml");
		XMLMotifReader r = new XMLMotifReader();
		DefaultMotif q = (DefaultMotif) r.read(new FileInputStream ("xml/multiple_clusters.xml"));
		GQL gql = new GQLImpl();
		ResultCollector rc = new ResultCollector();
		gql.query(g,q,rc);
		List<MotifInstance> results = rc.getInstances();
		assertEquals(4,results.size());
	}
	private Vertex getVertexById(Graph g,String id) {
		for (Object v:g.getVertices()) {
			if (((Vertex)v).getUserDatum("name").equals(id)){
				return (Vertex)v;
			}
		}
		return null;
	}
	private Edge getEdgeById(Graph g,String id) {
		for (Object v:g.getEdges()) {
			if (((Edge)v).getUserDatum("id").equals(id)){
				return (Edge)v;
			}
		}
		return null;
	}
	
	private LinkConstraint getConstraint(Motif q,String source,String target) {
		List<Constraint> constraints = q.getConstraints();
		for(Iterator itr = constraints.iterator();itr.hasNext();){
			Constraint c = (Constraint) itr.next();
			if(c instanceof PathConstraint){
				if(((PathConstraint) c).getSource().equals(source) && ((PathConstraint) c).getTarget().equals(target)){
					return (LinkConstraint) c;
				}
			} else if (c instanceof EdgeConstraint){
				if(((EdgeConstraint) c).getSource().equals(source) && ((EdgeConstraint) c).getTarget().equals(target)){
					return (LinkConstraint) c;
				}
			}
		}
		return null;
	}
	
}
