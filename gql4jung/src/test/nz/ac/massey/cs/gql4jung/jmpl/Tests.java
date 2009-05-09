package test.nz.ac.massey.cs.gql4jung.jmpl;

import static junit.framework.Assert.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
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
import org.junit.Test;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphMLFile;

/**
 * Tests for the new (jmpl) query engine implementation.
 * @author jens dietrich
 */

public class Tests {

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
	private void doTest(String motif, String data,Map<String, String> expected,boolean shouldSucceed) throws Exception {
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
	@Test
	public void testAWDAnt1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.apache.tools.ant.filters.TokenFilter");
		expected.put("service","org.apache.tools.ant.filters.TokenFilter$ChainableReaderFilter");
		expected.put("service_impl","org.apache.tools.ant.filters.TokenFilter$ContainsRegex");		
		doTest("awd.xml","ant.jar.graphml",expected,true);
	}
	@Test
	public void testAWDAnt2() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.apache.tools.ant.filters.util.ChainReaderHelper");
		expected.put("service","org.apache.tools.ant.filters.BaseFilterReader");
		expected.put("service_impl","org.apache.tools.ant.filters.LineContains");		
		doTest("awd.xml","ant.jar.graphml",expected,true);
	}
	@Test
	public void testAWD1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.example.AClient");
		expected.put("service","org.example.AService");
		expected.put("service_impl","org.example.AServiceImpl");		
		doTest("awd.xml","testdata-awd1.graphml",expected,true);
	}
	@Test
	public void testAWD2() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.example.AClient");
		expected.put("service","org.example.AService");
		expected.put("service_impl","org.example.AServiceImpl");		
		doTest("awd.xml","testdata-awd2.graphml",expected,true);
	}
	@Test
	public void testAWD3() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.example.AClient");
		expected.put("service","org.example.AService");
		expected.put("service_impl","org.example.AServiceImpl");		
		doTest("awd.xml","testdata-awd3.graphml",expected,true);
	}
	@Test
	public void testAWD4() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.example.AClient");
		expected.put("service","org.example.AService");
		expected.put("service_impl","org.example.AServiceImpl");		
		doTest("awd.xml","testdata-awd4.graphml",expected,true);
	}
	@Test
	public void testAWD5() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.example.AClient");
		expected.put("service","org.example.AService");
		expected.put("service_impl","org.example.AServiceImpl");		
		doTest("awd.xml","testdata-awd5.graphml",expected,true);
	}
	
	public void testCD1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("inside1","org.example1.Inside1");
		expected.put("outside1","org.example2.Outside1");
		expected.put("outside2","org.example2.Outside2");
		expected.put("inside2","org.example1.Inside2");
		doTest("cd.xml","testdata-cd1.graphml",expected,true);
	}
	
}
