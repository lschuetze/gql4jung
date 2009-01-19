package test.nz.ac.massey.cs.gpl4jung.gql;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Set;

import nz.ac.massey.cs.gpl4jung.constraints.EdgeConstraint;

import org.junit.Test;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

public class EdgeConstraintTests {
	Graph g = new DirectedSparseGraph();
	private void buildGraph(){
		Vertex v0 = new DirectedSparseVertex();
		Vertex v1 = new DirectedSparseVertex();
		Vertex v2 = new DirectedSparseVertex();
		Vertex v3 = new DirectedSparseVertex();
		Vertex v4 = new DirectedSparseVertex();
		Vertex v5 = new DirectedSparseVertex();
		Vertex v6 = new DirectedSparseVertex();
		Vertex v7 = new DirectedSparseVertex();
		Vertex v8 = new DirectedSparseVertex();
		Vertex v9 = new DirectedSparseVertex();
		g.addVertex(v0);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addVertex(v6);
		g.addVertex(v7);
		g.addVertex(v8);
		g.addVertex(v9);
		//creating edges for graph
		Edge e1 = new DirectedSparseEdge(v0, v1);
        Edge e2 = new DirectedSparseEdge(v2, v3);
        Edge e3 = new DirectedSparseEdge(v1, v4);
        Edge e4 = new DirectedSparseEdge(v3, v4);
        Edge e5 = new DirectedSparseEdge(v4, v5);
        Edge e6 = new DirectedSparseEdge(v4, v7);
        Edge e7 = new DirectedSparseEdge(v5, v6);
        Edge e8 = new DirectedSparseEdge(v7, v8);
        //adding edges to graph
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);
        g.addEdge(e7);
        g.addEdge(e8);
	}
	
	@Test
	public void testGetSource() {
		EdgeConstraint ec = new EdgeConstraint();
		String source="V0";
		ec.setSource(source);
		assertEquals(source, ec.getSource());
	}

	@Test
	public void testSetSource() {
		EdgeConstraint ec = new EdgeConstraint();
		String source="V0";
		ec.setSource(source);
		assertEquals(source, ec.getSource());
	}

	@Test
	public void testGetTarget() {
		EdgeConstraint ec = new EdgeConstraint();
		String target="V1";
		ec.setTarget(target);
		assertEquals(target, ec.getTarget());
	}

	@Test
	public void testSetTarget() {
		EdgeConstraint ec = new EdgeConstraint();
		String target="V1";
		ec.setTarget(target);
		assertEquals(target, ec.getTarget());
	}

	@Test
	public void testGetPossibleSourcesGraphVertex() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetPossibleTargetsGraphVertex() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testCheckGraphVertexVertex() {
		EdgeConstraint ec = new EdgeConstraint();
		buildGraph();
		Vertex testv1=null, testv4=null;
		for (Iterator iter = g.getEdges().iterator(); iter.hasNext();)
        {
        	Edge iedge =   (Edge) iter.next();
        	if(iedge.getEndpoints().getFirst().toString().equalsIgnoreCase("V1") && iedge.getEndpoints().getSecond().toString().equalsIgnoreCase("V4")){
        		 testv1= (Vertex) iedge.getEndpoints().getFirst();
        		 testv4= (Vertex) iedge.getEndpoints().getSecond();
        	}	
        }
		Edge testedge = ec.check(g, testv1, testv4);
		String testEdgeStr = testedge.toString();
		assertEquals("E2(V1,V4)", testEdgeStr);
	
	}

}
