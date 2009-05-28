package test.nz.ac.massey.cs.gql4jung.util;

import static junit.framework.Assert.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.GraphMLReader;
import nz.ac.massey.cs.gql4jung.util.ConstraintedShortestPathFinder;
import nz.ac.massey.cs.gql4jung.util.PathFinder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.google.common.base.Predicate;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;


/**
 * Abstract superclass for tests for the new (jmpl) query engine implementation.
 * @author jens dietrich
 */

public class Tests {
	private static Predicate<Edge> USES = new Predicate<Edge>() {
		@Override
		public boolean apply(Edge e) {
			return "uses".equals(e.getType());
		}	
	};
	private static Predicate<Edge> EXTENDS = new Predicate<Edge>() {
		@Override
		public boolean apply(Edge e) {
			return "extends".equals(e.getType());
		}	
	};
	private static Predicate<Edge> ALL = new Predicate<Edge>() {
		@Override
		public boolean apply(Edge e) {
			return true;
		}	
	};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private DirectedGraph<Vertex, Edge> loadGraph(String name) throws Exception {
        String src = "/test/nz/ac/massey/cs/gql4jung/util/data/"+name;
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(src));
        GraphMLReader greader = new GraphMLReader(reader);
        DirectedGraph<Vertex, Edge> g = greader.readGraph();
        greader.close();
        return g;
	}

	private void doTestLinks(String data,Collection<String[]> expectedPaths,boolean isOutgouing,String startHere,int minLength,int maxLength, Predicate<Edge> filter) throws Exception {
		DirectedGraph<Vertex,Edge> g = this.loadGraph(data);
		Vertex v = getVertex(g,startHere);
		Iterator<Path> iter = PathFinder.findLinks(g,v, minLength, maxLength, isOutgouing, filter);
		Collection<String[]> expected = new HashSet<String[]>();
		expected.addAll(expectedPaths);
		while (iter.hasNext()) {
			Path next = iter.next();
			String[] arr = toArray(next);
			boolean removed = false;
			Iterator<String[]> iter2=expected.iterator();
			while (!removed&&iter2.hasNext()) {
				if (Arrays.equals(iter2.next(),arr)) {
					iter2.remove();
					removed=true;
				}
			}
			if (!removed) {
				assertTrue("Did find unexpected path " + toString(arr),false);
			}
		}
		if (!expected.isEmpty()) {
			String[] arr = expected.iterator().next();
			assertTrue("Did not find expected path " + toString(arr),false);
		}
		else {
			assertTrue(true);
		}	
	}
	
	// check the shortest paths between nodes
	private void doTestShortestPath(String data,String[] expected,String start,String end,int minLength,int maxLength, Predicate<Edge> filter) throws Exception {
		DirectedGraph<Vertex,Edge> g = this.loadGraph(data);
		Vertex source = getVertex(g,start);
		Vertex target = getVertex(g,end);
		Path path = ConstraintedShortestPathFinder.findLink(g,source, target, minLength, maxLength, filter);
		String[] arr = path==null?null:toArray(path);
		Assert.assertArrayEquals("this path was not expected: " + this.toString(arr),expected,arr);
	}

	private String toString(String[] arr) {
		if (arr==null) return "null";
		boolean f = true;
		StringBuffer b = new StringBuffer();
		b.append("{");
		for (String s:arr) {
			if (f) f=false;
			else b.append(',');
			b.append("\"");
			b.append(s);
			b.append("\"");
		}
		b.append("}");
		return b.toString();
	}

	private String[] toArray(Path path) {
		List<Vertex> list = path.getVertices();
		String[] arr = new String[list.size()];
		for (int i=0;i<list.size();i++) {
			arr[i]=(String)list.get(i).getName();
		}
		return arr;
	}

	private Vertex getVertex(Graph g, String name) {
		for (Vertex v:(Collection<Vertex>)g.getVertices()) {
			if (v.getName().equals(name)) {
				return v;
			}
		}
		return null;
	}
	
	@Test
	public void testOut1() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v1","v2"});
		expected.add(new String[] {"v1","v3"});
		expected.add(new String[] {"v1","v3","v8"});
		expected.add(new String[] {"v1","v2","v4"});
		expected.add(new String[] {"v1","v2","v4","v5"});
		expected.add(new String[] {"v1","v2","v4","v5","v6"});
		expected.add(new String[] {"v1","v2","v4","v5","v7"});
		doTestLinks("graph1.graphml",expected,true,"v1",1,-1,ALL) ;
	}
	
	@Test
	public void testOut2() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v1","v2"});
		expected.add(new String[] {"v1","v2","v4"});
		expected.add(new String[] {"v1","v2","v4","v5"});
		expected.add(new String[] {"v1","v2","v4","v5","v6"});
		expected.add(new String[] {"v1","v2","v4","v5","v7"});
		doTestLinks("graph1.graphml",expected,true,"v1",1,-1,USES) ;
	}
	
	@Test
	public void testOut3() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v1","v3"});
		expected.add(new String[] {"v1","v3","v8"});
		doTestLinks("graph1.graphml",expected,true,"v1",1,-1,EXTENDS) ;
	}
	
	@Test
	public void testOut4() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v1"});
		expected.add(new String[] {"v1","v2"});
		expected.add(new String[] {"v1","v3"});
		expected.add(new String[] {"v1","v3","v8"});
		expected.add(new String[] {"v1","v2","v4"});
		expected.add(new String[] {"v1","v2","v4","v5"});
		expected.add(new String[] {"v1","v2","v4","v5","v6"});
		expected.add(new String[] {"v1","v2","v4","v5","v7"});
		doTestLinks("graph1.graphml",expected,true,"v1",0,-1,ALL) ;
	}
	
	@Test
	public void testOut5() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v1","v3","v8"});
		expected.add(new String[] {"v1","v2","v4"});
		expected.add(new String[] {"v1","v2","v4","v5"});
		expected.add(new String[] {"v1","v2","v4","v5","v6"});
		expected.add(new String[] {"v1","v2","v4","v5","v7"});
		doTestLinks("graph1.graphml",expected,true,"v1",2,-1,ALL) ;
	}
	@Test
	public void testOut6() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v1","v2"});
		expected.add(new String[] {"v1","v3"});
		expected.add(new String[] {"v1","v3","v8"});
		expected.add(new String[] {"v1","v2","v4"});
		expected.add(new String[] {"v1","v2","v4","v5"});
		doTestLinks("graph1.graphml",expected,true,"v1",1,3,ALL) ;
	}
	
	@Test
	public void testOut7() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v1","v2","v4"});
		expected.add(new String[] {"v1","v2","v4","v5"});
		doTestLinks("graph1.graphml",expected,true,"v1",2,3,USES) ;
	}
	
	@Test
	public void testIn1() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v2","v1"});
		expected.add(new String[] {"v3","v1"});
		expected.add(new String[] {"v8","v3","v1"});
		expected.add(new String[] {"v4","v2","v1"});
		expected.add(new String[] {"v5","v4","v2","v1"});
		expected.add(new String[] {"v6","v5","v4","v2","v1"});
		expected.add(new String[] {"v7","v5","v4","v2","v1"});
		doTestLinks("graph2.graphml",expected,false,"v1",1,-1,ALL) ;
	}
	
	@Test
	public void testIn2() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v2","v1"});
		expected.add(new String[] {"v4","v2","v1"});
		expected.add(new String[] {"v5","v4","v2","v1"});
		expected.add(new String[] {"v6","v5","v4","v2","v1"});
		expected.add(new String[] {"v7","v5","v4","v2","v1"});
		doTestLinks("graph2.graphml",expected,false,"v1",1,-1,USES) ;
	}
	
	@Test
	public void testIn3() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v3","v1"});
		expected.add(new String[] {"v8","v3","v1"});
		doTestLinks("graph2.graphml",expected,false,"v1",1,-1,EXTENDS) ;
	}
	
	@Test
	public void testIn4() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v1"});
		expected.add(new String[] {"v2","v1"});
		expected.add(new String[] {"v3","v1"});
		expected.add(new String[] {"v8","v3","v1"});
		expected.add(new String[] {"v4","v2","v1"});
		expected.add(new String[] {"v5","v4","v2","v1"});
		expected.add(new String[] {"v6","v5","v4","v2","v1"});
		expected.add(new String[] {"v7","v5","v4","v2","v1"});
		doTestLinks("graph2.graphml",expected,false,"v1",0,-1,ALL) ;
	}
	
	@Test
	public void testIn5() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v8","v3","v1"});
		expected.add(new String[] {"v4","v2","v1"});
		expected.add(new String[] {"v5","v4","v2","v1"});
		expected.add(new String[] {"v6","v5","v4","v2","v1"});
		expected.add(new String[] {"v7","v5","v4","v2","v1"});
		doTestLinks("graph2.graphml",expected,false,"v1",2,-1,ALL) ;
	}
	@Test
	public void testIn6() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v2","v1"});
		expected.add(new String[] {"v3","v1"});
		expected.add(new String[] {"v8","v3","v1"});
		expected.add(new String[] {"v4","v2","v1"});
		expected.add(new String[] {"v5","v4","v2","v1"});
		doTestLinks("graph2.graphml",expected,false,"v1",1,3,ALL) ;
	}
	
	@Test
	public void testIn7() throws Exception {
		List<String[]> expected = new ArrayList<String[]>();
		expected.add(new String[] {"v4","v2","v1"});
		expected.add(new String[] {"v5","v4","v2","v1"});
		doTestLinks("graph2.graphml",expected,false,"v1",2,3,USES) ;
	}
	
	@Test
	public void testShortestPath1() throws Exception {
		String[] expected = new String[]{"v1","v3","v7"};
		this.doTestShortestPath("graph3.graphml",expected,"v1","v7",0,-1,ALL);
	}
	@Test
	public void testShortestPath2() throws Exception {
		String[] expected = new String[]{"v1","v3","v7"};
		this.doTestShortestPath("graph3.graphml",expected,"v1","v7",0,-1,EXTENDS);
	}
	@Test
	public void testShortestPath3() throws Exception {
		String[] expected = new String[]{"v1","v2","v4","v7"};
		this.doTestShortestPath("graph3.graphml",expected,"v1","v7",0,-1,USES);
	}
	@Test
	public void testShortestPath4() throws Exception {
		String[] expected = new String[]{"v1","v2","v4","v7"};
		this.doTestShortestPath("graph3.graphml",expected,"v1","v7",3,-1,ALL);
	}
	@Test
	public void testShortestPath5() throws Exception {
		String[] expected = new String[]{"v1","v2","v5","v6","v7"};
		this.doTestShortestPath("graph3.graphml",expected,"v1","v7",4,-1,USES);
	}
	@Test
	public void testShortestPath6() throws Exception {
		String[] expected = null;
		this.doTestShortestPath("graph3.graphml",expected,"v1","v7",4,-1,EXTENDS);
	}
}
