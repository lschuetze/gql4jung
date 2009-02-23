package test.nz.ac.massey.cs.gpl4jung.gql;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nz.ac.massey.cs.gpl4jung.Constraint;
import nz.ac.massey.cs.gpl4jung.DefaultMotif;
import nz.ac.massey.cs.gpl4jung.GQL;
import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.MotifInstance;
import nz.ac.massey.cs.gpl4jung.Path;
import nz.ac.massey.cs.gpl4jung.PropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.PathConstraint;
import nz.ac.massey.cs.gpl4jung.impl.Bindings;
import nz.ac.massey.cs.gpl4jung.impl.ConstraintSchedulerImpl;
import nz.ac.massey.cs.gpl4jung.impl.GQLImpl;
import nz.ac.massey.cs.gpl4jung.xml.XMLMotifReader;
import nz.ac.massey.cs.utils.odem2graphml.Odem2GraphML;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.io.GraphMLFile;

public class GQLTests {
	static {
		// only necessary when running inside massey
		System.getProperties().put( "proxySet", "true" );
		System.getProperties().put( "proxyHost", "tur-cache" );
		System.getProperties().put( "proxyPort", "8080" );
	}
	
	
	private GQL gql = null; // TODO
	@Before
	public void init() {
		gql = new GQLImpl();
		
	}
	@After
	public void release() {
		this.gql=null;
		
	}
	// see also http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/io/GraphMLFile.html
	private Graph readJungGraphFromGraphML(String graphSource) throws Exception {
		GraphMLFile input = new GraphMLFile();
		Reader reader = new FileReader(graphSource);
		Graph g = input.load(reader);
		
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
//	@Test
//	//No decoupling through abstraction
//	public void test1 () throws Exception {
//		Graph g = this.readJungGraphFromGraphML("test_examples/abstraction.graphml");
//		DefaultMotif q = (DefaultMotif) readMotif("xml/query1.xml");
//		ResultCollector rc = new ResultCollector();
//		this.gql.query(g,q,rc);
//		List<MotifInstance> results = rc.getInstances();
//		
//		// when client "MyApplication" accesses both "Animal" abstract class & "Horse" Impl of animal
//		assertEquals(1,results.size());
//		MotifInstance instance1 = results.get(0);
//		assertEquals("Animal",instance1.getVertex("service").getUserDatum("name"));
//		assertEquals("MyApplication",instance1.getVertex("client").getUserDatum("name"));
//		assertEquals("Horse",instance1.getVertex("service_impl").getUserDatum("name"));
//		//assertEquals(expected, actual)
//	}
	
	@Test
	public void test1()throws Exception{
		Graph g = this.readJungGraphFromGraphML("test_examples/abstraction.graphml");
		Vertex v2 = null, v0=null, v1=null;
		Edge e1 = null;
	
		for (Object v:g.getVertices()) {
			if (((Vertex)v).getUserDatum("class.id").equals("2")) 
			v2=(Vertex)v;
			else if (((Vertex)v).getUserDatum("class.id").equals("0")) 
				v0=(Vertex)v;
			else if (((Vertex)v).getUserDatum("class.id").equals("1")) 
				v1=(Vertex)v;
				
			//System.out.println(v2.getUserDatum("id"));
		}
//		for (Object e:g.getEdges()){
//			if(((Edge)e).getUserDatum("id").equals("edge-1"))
//				e1 = (Edge) e;
//			//System.out.println(e1.getUserDatum("id"));
//		}
		DefaultMotif q = (DefaultMotif) readMotif("xml/query1.xml");
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
	
		
	
	@Test
	public void test2() throws Exception {
		Graph g = this.readJungGraphFromGraphML("test_examples/abstraction.graphml");
		Motif q = (DefaultMotif) readMotif("xml/query1.xml");
		GQLImpl gql = new GQLImpl();
		ResultCollector listener = new ResultCollector();
		gql.query(g, q, listener);
		// analyse results
		//assertEquals(listener.getInstances().size(),1);
		for (MotifInstance result:listener.getInstances()) {
			assertEquals(result.getVertex("client"),this.getVertexById(g,"2"));
			assertEquals(result.getVertex("service"),this.getVertexById(g,"0"));
			assertEquals(result.getVertex("service_impl"),this.getVertexById(g,"1"));
			Path p1 = (Path)result.getLink(getConstraint(q,"client","service"),g);
			assertTrue(p1.getEdges().contains(this.getEdgeById(g,"edge-4")));
			Path p2 = (Path)result.getLink(getConstraint(q,"client","service_impl"),g);
			assertTrue(p2.getEdges().contains(this.getEdgeById(g,"edge-5")));
			Path p3 = (Path)result.getLink(getConstraint(q,"service_impl","service"), g);
			assertTrue(p3.getEdges().contains(this.getEdgeById(g,"edge-1")));
		}
	}
	@Test
//	Testcase : circular dependency between classes and packages. 
	public void test3() throws Exception {
		Graph g = this.readJungGraphFromGraphML("test_examples/dependency.graphml");
		XMLMotifReader r = new XMLMotifReader();
		DefaultMotif q = (DefaultMotif) r.read(new FileInputStream ("xml/query2.xml"));
		ResultCollector rc = new ResultCollector();
		this.gql.query(g,q,rc);
		List<MotifInstance> results = rc.getInstances();
		
		assertEquals(1,results.size());
		MotifInstance instance1 = results.get(0);
		assertEquals("Class1",instance1.getVertex("Class1").getUserDatum("name"));
		assertEquals("Class3",instance1.getVertex("Class3").getUserDatum("name"));
		assertEquals("Class2",instance1.getVertex("Class2").getUserDatum("name"));
	}
	@Test
	// Testcase : Test for a class depending on both UI layer and DB layer
	
	public void test4() throws Exception {
		Graph g = this.readJungGraphFromGraphML("test_examples/separation.graphml");
		XMLMotifReader r = new XMLMotifReader();
		DefaultMotif q = (DefaultMotif) r.read(new FileInputStream ("xml/query3.xml"));
		ResultCollector rc = new ResultCollector();
		this.gql.query(g,q,rc);
		List<MotifInstance> results = rc.getInstances();
		
		assertEquals(1,results.size());
		MotifInstance instance1 = results.get(0);
		assertEquals("MyClass",instance1.getVertex("ui").getUserDatum("name"));
		assertEquals("MyClass",instance1.getVertex("db").getUserDatum("name"));
	}
	
	private Vertex getVertexById(Graph g,String id) {
		for (Object v:g.getVertices()) {
			if (((Vertex)v).getUserDatum("class.id").equals(id)){
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
			}
		}
		return null;
	}
	
}

