package test.nz.ac.massey.cs.gpl4jung.gql;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import nz.ac.massey.cs.gpl4jung.constraints.EdgeConstraint;

import org.apache.commons.collections.IteratorUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

public class EdgeConstraintTests {
	static Graph g = null; 
	@BeforeClass 
	public static void setUp(){
		g = new DirectedSparseGraph();	
		buildGraph();
	}
	@Test
	public void testGetPossibleSources() {
		EdgeConstraint ec = new EdgeConstraint();
		Vertex testV4 = getVertexFromGraph("V4"); // Target test vertex
		Vertex testV1 = getVertexFromGraph("V1"); // possible source vertex
		Vertex testV3 = getVertexFromGraph("V3"); // possible source vertex
		//obtaining possible link to populate ConnectedVertex<Edge> for testing
		Edge linkV1 = testV1.findEdge(testV4);
		ConnectedVertex<Edge> v1 = new ConnectedVertex<Edge>(linkV1, testV1); //to compare with actual result
		//obtaining possible link to populate ConnectedVertex<Edge> for testing
		Edge linkV3 = testV3.findEdge(testV4);
		ConnectedVertex<Edge> v3 = new ConnectedVertex<Edge>(linkV3, testV3); //to compare with actual result
		//method to test getPossibleSources(graph, target_vertex)
		Iterator<ConnectedVertex<Edge>> ps =  ec.getPossibleSources(g, testV4);	
		List<ConnectedVertex<Edge>> list = IteratorUtils.toList(ps); //list of all possible sources
		//Asserts to compare results (expected and actual) 
		assertEquals(2, list.size());
		assertTrue(list.contains(v1)); //v1 expected possible source 
		assertTrue(list.contains(v3)); //v3 expected possible source
	}
	@Test
	public void testGetPossibleTargets() {
		EdgeConstraint ec = new EdgeConstraint();
		Vertex testV4 = getVertexFromGraph("V4"); // Source test vertex
		Vertex testV5 = getVertexFromGraph("V5"); // Possible Target vertex
		Vertex testV7 = getVertexFromGraph("V7"); // Possible Target vertex
		//obtaining possible link to populate ConnectedVertex<Edge> for testing
		Edge linkV5 = testV4.findEdge(testV5);
		ConnectedVertex<Edge> v5 = new ConnectedVertex<Edge>(linkV5, testV5); //to compare with actual result
		//obtaining possible link to populate ConnectedVertex<Edge> for testing
		Edge linkV7 = testV4.findEdge(testV7);
		ConnectedVertex<Edge> v7 = new ConnectedVertex<Edge>(linkV7, testV7); //to compare with actual result
		//method to test getPossibleTargets(graph, source_vertex)
		Iterator<ConnectedVertex<Edge>> ps =  ec.getPossibleTargets(g, testV4);	
		List<ConnectedVertex<Edge>> list = IteratorUtils.toList(ps); //list of all possible targets
		//Asserts to compare results (expected and actual) 
		assertEquals(2, list.size());
		assertTrue(list.contains(v5)); //v5 expected possible source 
		assertTrue(list.contains(v7)); //v7 expected possible source
	}
	@Test
	public void testCheck() {
		EdgeConstraint ec = new EdgeConstraint();
		Vertex testV1 = getVertexFromGraph("V1"); // Source test vertex
		Vertex testV4 = getVertexFromGraph("V4"); // Target test vertex
		Vertex expectedV1 = getVertexFromGraph("V1"); // Expected Source vertex
		Vertex expectedV4 = getVertexFromGraph("V4"); // Expected Target vertex
		//method to test for an edge "check(g,source,target)"
		Edge testedge = ec.check(g, testV1, testV4);
        //getting edge label into string
		Edge expectedEdge = expectedV1.findEdge(expectedV4);
		//Assert for result, change value of expectedV1 or expected V2 to see difference
		assertEquals(expectedEdge, testedge);
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
