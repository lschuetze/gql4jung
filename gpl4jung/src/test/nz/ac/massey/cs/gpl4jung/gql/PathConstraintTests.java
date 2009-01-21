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
		Vertex testV4 = getVertexFromGraph("V4"); // Target test vertex
		Vertex testV0 = getVertexFromGraph("V0"); // possible source vertex
		Vertex testV1 = getVertexFromGraph("V1"); // possible source vertex
		Vertex testV2 = getVertexFromGraph("V2"); // possible source vertex
		Vertex testV3 = getVertexFromGraph("V3"); // possible source vertex
		//method to test getPossibleSources(graph, target_vertex) 
		Iterator<ConnectedVertex<Path>> ps =  pc.getPossibleSources(g, testV4);
		List<ConnectedVertex<Path>> list = IteratorUtils.toList(ps);
		//obtaining link,vertex for connected vertex and intializing it for v1
		
		//List path = ShortestPathUtils.getPath(SPA,connectedv1,testv4);
		//PathImpl p = new PathImpl();
		//p.setEdges((List<Edge>) path);
		//ConnectedVertex<Path> v1 = new ConnectedVertex<Path>(p, connectedv1);
		
		assertEquals(4, list.size());
		//assertTrue(list.contains(v1));
		
	}

	@Test
	public void testGetPossibleTargets() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testCheck() {
		PathConstraint pc = new PathConstraint();
		Vertex sourceV0=null, targetV4=null;
		//getting source vertex from graph
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.toString().equalsIgnoreCase("V0")){
				sourceV0 = (Vertex) v;
			}
		}
		//getting target vertex from graph
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.toString().equalsIgnoreCase("V4")){
				targetV4 = (Vertex) v;
			}
		}
		Path testpath = pc.check(g, sourceV0, targetV4);
		
		String result = "[E0(V0,V1), E2(V1,V4)]";
		assertEquals(result, testpath.getEdges().toString());
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
		Edge e1 = g.addEdge(new DirectedSparseEdge(v0, v1));
        Edge e2 = g.addEdge(new DirectedSparseEdge(v2, v3));
        Edge e3 = g.addEdge(new DirectedSparseEdge(v1, v4));
        Edge e4 = g.addEdge(new DirectedSparseEdge(v3, v4));
        Edge e5 = g.addEdge(new DirectedSparseEdge(v4, v5));
        Edge e6 = g.addEdge(new DirectedSparseEdge(v4, v7));
        Edge e7 = g.addEdge(new DirectedSparseEdge(v5, v6));
        Edge e8 = g.addEdge(new DirectedSparseEdge(v7, v8));  
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
