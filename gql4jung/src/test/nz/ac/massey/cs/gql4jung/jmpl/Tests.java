package test.nz.ac.massey.cs.gql4jung.jmpl;

import static junit.framework.Assert.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.jmpl.GQLImpl;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphMLFile;

/**
 * Abstract superclass for tests for the new (jmpl) query engine implementation.
 * @author jens dietrich
 */

public abstract class Tests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	protected Graph loadGraph(String name) throws Exception {
		GraphMLFile input = new GraphMLFile();
        String src = "/test/nz/ac/massey/cs/gql4jung/jmpl/data/"+name;
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(src));
        Graph g = new DirectedSparseGraph();
        g =	input.load(reader);
        reader.close();
        return g;
	}
	protected Motif loadQuery(String name) throws Exception {
		String src = "/test/nz/ac/massey/cs/gql4jung/jmpl/queries/"+name;
        return new XMLMotifReader().read(this.getClass().getResourceAsStream(src));
	}
	// expected associates roles with full class names
	protected boolean check(List<MotifInstance> instances,Map<String,String> expected) {
		for (MotifInstance inst:instances) {
			boolean result = true;
			for (String role:expected.keySet()) {
				Vertex v = inst.getVertex(role);
				if (result) {
					String classname = ""+v.getUserDatum("namespace")+'.'+v.getUserDatum("name");
					result = result && classname.equals(expected.get(role));
				}
			}
			if (result) return true;
		}
		return false;
	} 
	// check whether a result specified as map is or is not in the result set
	protected void doTest(String motif, String data,Map<String, String> expected,boolean shouldSucceed) throws Exception {
		Graph g = this.loadGraph(data);
		Motif m = this.loadQuery(motif);
		ResultCollector coll = new ResultCollector();
		GQL engine = new GQLImpl();
		long t1 = System.currentTimeMillis();
		engine.query(g,m,coll);
		long t2 = System.currentTimeMillis();
		System.out.println("query "+motif+" on data "+data+ " returned "+coll.getInstances().size()+" results");
		System.out.println("query "+motif+" on data "+data+ " took "+(t2-t1)+" millis");
		assertTrue(shouldSucceed==check(coll.getInstances(),expected));
	}
	
	
	// check the expected number of results
	protected void doTest(String motif, String data,int expected) throws Exception {
		Graph g = this.loadGraph(data);
		Motif m = this.loadQuery(motif);
		ResultCollector coll = new ResultCollector();
		GQL engine = new GQLImpl();
		long t1 = System.currentTimeMillis();
		engine.query(g,m,coll);
		long t2 = System.currentTimeMillis();
		System.out.println("query "+motif+" on data "+data+ " returned "+coll.getInstances().size()+" results");
		System.out.println("query "+motif+" on data "+data+ " took "+(t2-t1)+" millis");
		assertEquals(expected,coll.getInstances().size());
	}
}
