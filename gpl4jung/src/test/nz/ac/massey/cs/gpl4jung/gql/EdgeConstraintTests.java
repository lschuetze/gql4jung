package test.nz.ac.massey.cs.gpl4jung.gql;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import nz.ac.massey.cs.gpl4jung.constraints.EdgeConstraint;

import org.apache.commons.collections.IteratorUtils;
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
	public void testGetPossibleSources() {
		EdgeConstraint ec = new EdgeConstraint();
		buildGraph();
		Vertex testv4 = null, connectedv1=null, connectedv3=null;
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.toString().equalsIgnoreCase("V4")){
				testv4 = (Vertex) v;
			}
		}
		Iterator<ConnectedVertex<Edge>> ps =  ec.getPossibleSources(g, testv4);	//TODO: DISCUSS
		
		List<ConnectedVertex<Edge>> list = IteratorUtils.toList(ps);
		System.out.println(list);
		//obtaining link,vertex for connected vertex and intializing it for v1
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.toString().equalsIgnoreCase("V1")){
				connectedv1 = (Vertex) v;
			}
		}
		Edge connectedlink = connectedv1.findEdge(testv4);
		ConnectedVertex<Edge> v1 = new ConnectedVertex<Edge>(connectedlink, connectedv1);
		
		//obtaining link,vertex for connected vertex and intializing it for v3
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.toString().equalsIgnoreCase("V3")){
				connectedv3 = (Vertex) v;
			}
		}
		Edge connectedlink2 = connectedv1.findEdge(testv4);
		ConnectedVertex<Edge> v3 = new ConnectedVertex<Edge>(connectedlink2, connectedv3);
		
		//assertEquals(2, list.size());
		assertTrue(list.equals(v1));
		assertTrue(list.contains(v3));
	}

	@Test
	public void testGetPossibleTargets() {
		EdgeConstraint ec = new EdgeConstraint();
		buildGraph();
		Vertex testv4 = null;
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.toString().equalsIgnoreCase("V4")){
				testv4 = (Vertex) v;
			}
		}
		Iterator<ConnectedVertex<Edge>> ps =  ec.getPossibleTargets(g, testv4);
		List<ConnectedVertex<Edge>> list = IteratorUtils.toList(ps);
		System.out.println(list); //should print [v5, v7]
		assertEquals(2, list.size());
	}

	@Test
	public void testCheck() {
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
        EdgeStringer stringer = new EdgeStringer(){

			@Override
			public String getLabel(ArchetypeEdge e) {
				return e.toString();
			}
        	
        };
		assertEquals("E2(V1,V4)", stringer.getLabel(testedge));
	
	}

}
