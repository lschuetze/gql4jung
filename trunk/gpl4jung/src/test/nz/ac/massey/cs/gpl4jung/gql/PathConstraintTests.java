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
		PathConstraint pc = new PathConstraint();
		buildGraph();
		Vertex testv4 = null, connectedv1=null;
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.toString().equalsIgnoreCase("V4")){
				testv4 = (Vertex) v;
			}
		}
		Iterator<ConnectedVertex<Path>> ps =  pc.getPossibleSources(g, testv4);
		List<ConnectedVertex<Path>> list = IteratorUtils.toList(ps);
		//obtaining link,vertex for connected vertex and intializing it for v1
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.toString().equalsIgnoreCase("V1")){
				connectedv1 = (Vertex) v;
			}
		}
		ShortestPath SPA = new DijkstraShortestPath(g);
		List path = ShortestPathUtils.getPath(SPA,connectedv1,testv4);
		PathImpl p = new PathImpl();
		p.setEdges((List<Edge>) path);
		ConnectedVertex<Path> v1 = new ConnectedVertex<Path>(p, connectedv1);
		assertTrue(list.equals(v1));
		//assertEquals(10, list.size());
	
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
