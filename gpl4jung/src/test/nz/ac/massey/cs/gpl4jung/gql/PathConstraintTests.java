package test.nz.ac.massey.cs.gpl4jung.gql;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import nz.ac.massey.cs.gpl4jung.Path;
import nz.ac.massey.cs.gpl4jung.constraints.PathConstraint;
import nz.ac.massey.cs.gpl4jung.impl.PathImpl;

import org.apache.commons.collections.IteratorUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPathUtils;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;

public class PathConstraintTests {
	static Graph g = null; 
	@BeforeClass 
	public static void setUp(){
		g = new DirectedSparseGraph();	
		buildGraph();
	}
	@Test
	public void testGetPossibleSources() {
		PathConstraint pc = new PathConstraint();
		ShortestPath SPA = new DijkstraShortestPath(g);
		PathImpl p0= new PathImpl(), p1=new PathImpl(), p2= new PathImpl(), p3= new PathImpl();
		PathImpl p5= new PathImpl(), p6=new PathImpl(), p7= new PathImpl(), p8= new PathImpl(), p9= new PathImpl();
		Vertex testV4 = getVertexFromGraph(g,"V4"); // Target test vertex
		Vertex testV0 = getVertexFromGraph(g,"V0"); // possible source vertex
		Vertex testV1 = getVertexFromGraph(g,"V1"); // possible source vertex
		Vertex testV2 = getVertexFromGraph(g,"V2"); // possible source vertex
		Vertex testV3 = getVertexFromGraph(g,"V3"); // possible source vertex
		Vertex testV5 = getVertexFromGraph(g,"V5"); // not a possible source vertex
		Vertex testV6 = getVertexFromGraph(g,"V6"); // not a possible source vertex
		Vertex testV7 = getVertexFromGraph(g,"V7"); // not a possible source vertex
		Vertex testV8 = getVertexFromGraph(g,"V8"); // not a possible source vertex
		Vertex testV9 = getVertexFromGraph(g,"V9"); // not a possible source vertex
		//method to test getPossibleSources(graph, target_vertex) 
		Iterator<ConnectedVertex<Path>> ps =  pc.getPossibleSources(g, testV4);
		List<ConnectedVertex<Path>> list = IteratorUtils.toList(ps);
		//test for 1st possible source for vertex V0 to target vertex V4
		List path = ShortestPathUtils.getPath(SPA,testV0,testV4);
		p0.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v0 = new ConnectedVertex<Path>(p0, testV0);
		//test for 2nd possible source for Vertex V1 to target vertex V4
		path = ShortestPathUtils.getPath(SPA,testV1,testV4);
		p1.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v1 = new ConnectedVertex<Path>(p1, testV1);
		//test for 3rd possible source for Vertex V2 to target vertex V4
		path = ShortestPathUtils.getPath(SPA,testV2,testV4);
		p2.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v2 = new ConnectedVertex<Path>(p2, testV2);
		//test for 4th possible source for Vertex V3 to target vertex V4
		path = ShortestPathUtils.getPath(SPA,testV3,testV4);
		p3.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v3 = new ConnectedVertex<Path>(p3, testV3);
		//test for 5th not possible source for Vertex V5 to target vertex V4
		path = ShortestPathUtils.getPath(SPA,testV5,testV4);
		p5.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v5 = new ConnectedVertex<Path>(p5, testV5);
		//test for 6th not possible source for Vertex V6 to target vertex V4
		path = ShortestPathUtils.getPath(SPA,testV6,testV4);
		p6.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v6 = new ConnectedVertex<Path>(p6, testV6);
		//test for 7th not possible source for Vertex V7 to target vertex V4
		path = ShortestPathUtils.getPath(SPA,testV7,testV4);
		p7.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v7 = new ConnectedVertex<Path>(p7, testV7);
		//test for 8th not possible source for Vertex V8 to target vertex V4
		path = ShortestPathUtils.getPath(SPA,testV8,testV4);
		p8.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v8 = new ConnectedVertex<Path>(p8, testV8);
		//test for false negative: 9th vertex not connected to any one
		path = ShortestPathUtils.getPath(SPA,testV9,testV4);
		p9.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v9 = new ConnectedVertex<Path>(p9, testV9);
		//Asserts
		assertEquals(4, list.size());
		assertTrue(list.contains(v0));
		assertTrue(list.contains(v1));
		assertTrue(list.contains(v2));
		assertTrue(list.contains(v3));
		//these vertcies should not be in possible sources so using assertFalse
		assertFalse(list.contains(v5));
		assertFalse(list.contains(v6));
		assertFalse(list.contains(v7));
		assertFalse(list.contains(v8));
		assertFalse(list.contains(v9));
	}

	@Test
	public void testGetPossibleTargets() {
		PathConstraint pc = new PathConstraint();
		ShortestPath SPA = new DijkstraShortestPath(g);
		PathImpl p0= new PathImpl(), p1=new PathImpl(), p2= new PathImpl(), p3= new PathImpl();
		PathImpl p5= new PathImpl(), p6=new PathImpl(), p7= new PathImpl(), p8= new PathImpl(), p9= new PathImpl();
		Vertex testV4 = getVertexFromGraph(g,"V4"); // Source test vertex
		Vertex testV0 = getVertexFromGraph(g,"V0"); // not a possible target vertex
		Vertex testV1 = getVertexFromGraph(g,"V1"); // not a possible target vertex
		Vertex testV2 = getVertexFromGraph(g,"V2"); // not a possible target vertex
		Vertex testV3 = getVertexFromGraph(g,"V3"); // not a possible target vertex
		Vertex testV5 = getVertexFromGraph(g,"V5"); // possible target vertex
		Vertex testV6 = getVertexFromGraph(g,"V6"); // possible target vertex
		Vertex testV7 = getVertexFromGraph(g,"V7"); // possible target vertex
		Vertex testV8 = getVertexFromGraph(g,"V8"); // possible target vertex
		Vertex testV9 = getVertexFromGraph(g,"V9"); // not a possible target vertex
		//method to test getPossibleTargets(graph, source_vertex) 
		Iterator<ConnectedVertex<Path>> ps =  pc.getPossibleTargets(g, testV4);
		List<ConnectedVertex<Path>> list = IteratorUtils.toList(ps);
		//test for 1st possible target for vertex V5 from source vertex V4
		List path = ShortestPathUtils.getPath(SPA,testV4,testV5);
		p5.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v5 = new ConnectedVertex<Path>(p5, testV5);
		//test for 2nd possible target for vertex V6 from source vertex V4
		path = ShortestPathUtils.getPath(SPA,testV4,testV6);
		p6.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v6 = new ConnectedVertex<Path>(p6, testV6);
		//test for 3rd possible target for vertex V7 from source vertex V4
		path = ShortestPathUtils.getPath(SPA,testV4,testV7);
		p7.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v7 = new ConnectedVertex<Path>(p7, testV7);
		//test for 4th possible target for vertex V8 from source vertex V4
		path = ShortestPathUtils.getPath(SPA,testV4,testV8);
		p8.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v8 = new ConnectedVertex<Path>(p8, testV8);
		
		//test for 1st not possible target vertex V0: to source vertex V4
		path = ShortestPathUtils.getPath(SPA,testV4,testV0);
		p0.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v0 = new ConnectedVertex<Path>(p0, testV0);
		//test for 2nd not possible target vertex V1: to source vertex V4
		path = ShortestPathUtils.getPath(SPA,testV4,testV1);
		p1.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v1 = new ConnectedVertex<Path>(p1, testV1);
		//test for 3rd not possible target vertex V2: to source vertex V4
		path = ShortestPathUtils.getPath(SPA,testV4,testV2);
		p2.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v2 = new ConnectedVertex<Path>(p2, testV2);
		//test for 4th not possible target vertex V3: to source vertex V4
		path = ShortestPathUtils.getPath(SPA,testV4,testV3);
		p3.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v3 = new ConnectedVertex<Path>(p3, testV3);
		//false-negative: test for 5th not possible target vertex V9: to source vertex V4
		path = ShortestPathUtils.getPath(SPA,testV4,testV3);
		p9.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v9 = new ConnectedVertex<Path>(p9, testV9);
		//Asserts
		assertEquals(4, list.size());
		assertTrue(list.contains(v5));
		assertTrue(list.contains(v6));
		assertTrue(list.contains(v7));
		assertTrue(list.contains(v8));
		//following vertcies should not be in possible targets so using assertFalse
		assertFalse(list.contains(v0));
		assertFalse(list.contains(v1));
		assertFalse(list.contains(v2));
		assertFalse(list.contains(v3));
		assertFalse(list.contains(v9));
	}

	@Test
	public void testCheck() {
		PathConstraint pc = new PathConstraint();
		Vertex testV0 = getVertexFromGraph(g,"V0"); // Source test vertex
		Vertex testV4 = getVertexFromGraph(g,"V4"); // Target test vertex
		Path testpath = pc.check(g, testV0, testV4);
		//obtaining path to compare for results
		ShortestPath SPA = new DijkstraShortestPath(g);
		PathImpl p = new PathImpl();
		Vertex expectedV0 = getVertexFromGraph(g, "V0"); // Expected Source vertex
		Vertex expectedV4 = getVertexFromGraph(g, "V4"); // Expected Target vertex
		List path = ShortestPathUtils.getPath(SPA,expectedV0,expectedV4);
		p.setEdges((List<Edge>) path);
		//Assert to compare results
		assertEquals(path, testpath.getEdges());
	}
	@Test
	public void testCheck1() {
		Graph g1 = buildGraph1();
		PathConstraint pc = new PathConstraint();
		Vertex testV0 = getVertexFromGraph(g1, "V0"); // Source test vertex
		Vertex testV4 = getVertexFromGraph(g1, "V4"); // 1st Target test vertex
		Vertex testV7 = getVertexFromGraph(g1, "V7"); // 2nd target vertex
		pc.setMaxLength(4);
		Path testpath = pc.check(g1, testV0, testV4);//length of path is 4 from V0-V4
		assertEquals(testpath, null);
		//assertTrue(testpath.getEdges().size()==4);
		
		//obtaining path to compare for results
		ShortestPath SPA = new DijkstraShortestPath(g1);
		PathImpl p = new PathImpl();
		Vertex expectedV0 = getVertexFromGraph(g1, "V0"); // Expected Source vertex
		Vertex expectedV7 = getVertexFromGraph(g1, "V7"); // Expected Target vertex
		List path = ShortestPathUtils.getPath(SPA,expectedV0,expectedV7);
		p.setEdges((List<Edge>) path);
		Path testpath1 = pc.check(g1, testV0, testV7);//length of path is 3 from V0-V7
		//Assert to compare results
		assertEquals(p.getEdges(), testpath1.getEdges());
		assertTrue(testpath1.getEdges().size()==3);
		
	}
	private static void buildGraph(){
		String key = "name";
		//creating vertices for graph
		Vertex v0 = g.addVertex(new DirectedSparseVertex());
		Vertex v1 = g.addVertex(new DirectedSparseVertex());
		Vertex v2 = g.addVertex(new DirectedSparseVertex());
		Vertex v3 = g.addVertex(new DirectedSparseVertex());
		Vertex v4 = g.addVertex(new DirectedSparseVertex());
		Vertex v5 = g.addVertex(new DirectedSparseVertex());
		Vertex v6 = g.addVertex(new DirectedSparseVertex());
		Vertex v7 = g.addVertex(new DirectedSparseVertex());
		Vertex v8 = g.addVertex(new DirectedSparseVertex());
		Vertex v9 = g.addVertex(new DirectedSparseVertex());
		//setting up vertices names...
		v0.addUserDatum(key, "V0", UserData.SHARED);
		v1.addUserDatum(key, "V1", UserData.SHARED);
		v2.addUserDatum(key, "V2", UserData.SHARED);
		v3.addUserDatum(key, "V3", UserData.SHARED);
		v4.addUserDatum(key, "V4", UserData.SHARED);
		v5.addUserDatum(key, "V5", UserData.SHARED);
		v6.addUserDatum(key, "V6", UserData.SHARED);
		v7.addUserDatum(key, "V7", UserData.SHARED);
		v8.addUserDatum(key, "V8", UserData.SHARED);
		v9.addUserDatum(key, "V9", UserData.SHARED);
		
		//creating edges for graph
		g.addEdge(new DirectedSparseEdge(v0, v1));
        g.addEdge(new DirectedSparseEdge(v2, v3));
        g.addEdge(new DirectedSparseEdge(v1, v4));
        g.addEdge(new DirectedSparseEdge(v3, v4));
        g.addEdge(new DirectedSparseEdge(v4, v5));
        g.addEdge(new DirectedSparseEdge(v4, v7));
        g.addEdge(new DirectedSparseEdge(v5, v6));
        g.addEdge(new DirectedSparseEdge(v7, v8));   
	}
	private Graph buildGraph1(){
		Graph graph = new DirectedSparseGraph();
		String key = "name";
		//creating vertices for graph
		Vertex v0 = graph.addVertex(new DirectedSparseVertex());
		Vertex v1 = graph.addVertex(new DirectedSparseVertex());
		Vertex v2 = graph.addVertex(new DirectedSparseVertex());
		Vertex v3 = graph.addVertex(new DirectedSparseVertex());
		Vertex v4 = graph.addVertex(new DirectedSparseVertex());
		Vertex v5 = graph.addVertex(new DirectedSparseVertex());
		Vertex v6 = graph.addVertex(new DirectedSparseVertex());
		Vertex v7 = graph.addVertex(new DirectedSparseVertex());
		
		//setting up vertices names...
		v0.addUserDatum(key, "V0", UserData.SHARED);
		v1.addUserDatum(key, "V1", UserData.SHARED);
		v2.addUserDatum(key, "V2", UserData.SHARED);
		v3.addUserDatum(key, "V3", UserData.SHARED);
		v4.addUserDatum(key, "V4", UserData.SHARED);
		v5.addUserDatum(key, "V5", UserData.SHARED);
		v6.addUserDatum(key, "V6", UserData.SHARED);
		v7.addUserDatum(key, "V7", UserData.SHARED);
		
		
		//creating edges for graph
		graph.addEdge(new DirectedSparseEdge(v0, v1));
		graph.addEdge(new DirectedSparseEdge(v1, v2));
		graph.addEdge(new DirectedSparseEdge(v2, v3));
        graph.addEdge(new DirectedSparseEdge(v3, v4));
        graph.addEdge(new DirectedSparseEdge(v0, v5));
        graph.addEdge(new DirectedSparseEdge(v5, v6));
        graph.addEdge(new DirectedSparseEdge(v6, v7));   
        
        return graph;
	}
	
	
	
	private Vertex getVertexFromGraph(Graph g, String vertexname){
		String key="name";
		Vertex testv = null;
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.getUserDatum(key).equals(vertexname)){
				testv = (Vertex) v;
			}
		}
		return testv;
	}
}
