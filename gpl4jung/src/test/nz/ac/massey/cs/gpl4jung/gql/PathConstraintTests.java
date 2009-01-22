package test.nz.ac.massey.cs.gpl4jung.gql;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import nz.ac.massey.cs.gpl4jung.Path;
import nz.ac.massey.cs.gpl4jung.constraints.EdgeConstraint;
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
		Vertex testV4 = getVertexFromGraph("V4"); // Target test vertex
		Vertex testV0 = getVertexFromGraph("V0"); // possible source vertex
		Vertex testV1 = getVertexFromGraph("V1"); // possible source vertex
		Vertex testV2 = getVertexFromGraph("V2"); // possible source vertex
		Vertex testV3 = getVertexFromGraph("V3"); // possible source vertex
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
		//Asserts
		assertEquals(4, list.size());
		assertTrue(list.contains(v0));
		assertTrue(list.contains(v1));
		assertTrue(list.contains(v2));
		assertTrue(list.contains(v3));
	}

	@Test
	public void testGetPossibleTargets() {
		PathConstraint pc = new PathConstraint();
		ShortestPath SPA = new DijkstraShortestPath(g);
		PathImpl p5= new PathImpl(), p6=new PathImpl(), p7= new PathImpl(), p8= new PathImpl();
		Vertex testV4 = getVertexFromGraph("V4"); // Source test vertex
		Vertex testV5 = getVertexFromGraph("V5"); // possible target vertex
		Vertex testV6 = getVertexFromGraph("V6"); // possible target vertex
		Vertex testV7 = getVertexFromGraph("V7"); // possible target vertex
		Vertex testV8 = getVertexFromGraph("V8"); // possible target vertex
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
		//Asserts
		assertEquals(4, list.size());
		assertTrue(list.contains(v5));
		assertTrue(list.contains(v6));
		assertTrue(list.contains(v7));
		assertTrue(list.contains(v8));
	}

	@Test
	public void testCheck() {
		PathConstraint pc = new PathConstraint();
		Vertex testV0 = getVertexFromGraph("V0"); // Source test vertex
		Vertex testV4 = getVertexFromGraph("V4"); // Target test vertex
		Path testpath = pc.check(g, testV0, testV4);
		//obtaining path to compare for results
		ShortestPath SPA = new DijkstraShortestPath(g);
		PathImpl p = new PathImpl();
		Vertex expectedV0 = getVertexFromGraph("V0"); // Expected Source vertex
		Vertex expectedV4 = getVertexFromGraph("V4"); // Expected Target vertex
		List path = ShortestPathUtils.getPath(SPA,expectedV0,expectedV4);
		p.setEdges((List<Edge>) path);
		//Assert to compare results
		assertEquals(p, testpath);
	}
	private static void buildGraph(){
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
	private Vertex getVertexFromGraph(String vertexname){
		Vertex testv = null;
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.toString().equalsIgnoreCase(vertexname)){
				testv = (Vertex) v;
			}
		}
		return testv;
	}
}
