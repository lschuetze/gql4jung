package test.nz.ac.massey.cs.gpl4jung.gql;

import static org.junit.Assert.*;

import java.util.Iterator;

import nz.ac.massey.cs.gpl4jung.Path;
import nz.ac.massey.cs.gpl4jung.constraints.PathConstraint;
import nz.ac.massey.cs.gpl4jung.impl.PathImpl;

import org.junit.Test;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

public class PathConstraintTests {
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
	public void testGetFrom() {
		PathConstraint pc = new PathConstraint();
		String str = "V1";
		pc.setFrom(str);
		String actual = pc.getFrom();
		assertEquals(str, actual);
	}

	@Test
	public void testSetFrom() {
		PathConstraint pc = new PathConstraint();
		String str = "V2";
		pc.setFrom(str);
		String actual = pc.getFrom();
		assertEquals(str, actual);
	}

	@Test
	public void testGetTo() {
		PathConstraint pc = new PathConstraint();
		String str = "V3";
		pc.setTo(str);
		String actual = pc.getTo();
		assertEquals(str, actual);
	}

	@Test
	public void testSetTo() {
		PathConstraint pc = new PathConstraint();
		String str = "V4";
		pc.setTo(str);
		String actual = pc.getTo();
		assertEquals(str, actual);
	}

	@Test
	public void testGetMaxLength() {
		PathConstraint pc = new PathConstraint();
		Integer maxLength = 5;
		pc.setMaxLength(maxLength);
		Integer actual = pc.getMaxLength();
		assertEquals(maxLength, actual);
	}

	@Test
	public void testSetMaxLength() {
		PathConstraint pc = new PathConstraint();
		Integer maxLength = 55;
		pc.setMaxLength(maxLength);
		Integer actual = pc.getMaxLength();
		assertEquals(maxLength, actual);
	}

	@Test
	public void testGetMinLength() {
		PathConstraint pc = new PathConstraint();
		Integer minLength = 0;
		pc.setMinLength(minLength);
		Integer actual = pc.getMinLength();
		assertEquals(minLength, actual);
	}

	@Test
	public void testSetMinLength() {
		PathConstraint pc = new PathConstraint();
		Integer minLength = -5;
		pc.setMinLength(minLength);
		Integer actual = pc.getMinLength();
		assertEquals(minLength, actual);
	}

	@Test
	public void testGetPossibleSources() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetPossibleTargets() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testCheck() {
		PathConstraint pc = new PathConstraint();
		buildGraph();
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

}
