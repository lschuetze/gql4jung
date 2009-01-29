package test.nz.ac.massey.cs.gpl4jung.gql;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Set;

import nz.ac.massey.cs.gpl4jung.constraints.PropertyTerm;
import nz.ac.massey.cs.gpl4jung.constraints.SimplePropertyConstraint;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;

public class PropertyConstraintTests {
	static Graph g = null; 
	@BeforeClass 
	public static void setUp(){
		g = new DirectedSparseGraph();	
		buildGraph();
	}
	@Test
	public void testCheck() {
		SimplePropertyConstraint<Vertex> vc = new SimplePropertyConstraint<Vertex>();
		Vertex testV1 = getVertexFromGraph("V1");
		assertTrue(vc.check(g, testV1));
	}
	
	private static void buildGraph(){
		//keys definition
		String name = "name";
		String type = "type";
		String isAbstract="isAbstract";
		
		//creating vertices for graph
		Vertex v0 = g.addVertex(new DirectedSparseVertex());
		Vertex v1 = g.addVertex(new DirectedSparseVertex());
		Vertex v2 = g.addVertex(new DirectedSparseVertex());
		Vertex v3 = g.addVertex(new DirectedSparseVertex());
		
		//setting up vertices names and other properties e.g. (type, isAbstract etc)
		v0.addUserDatum(name, "V0", UserData.SHARED);
		v0.addUserDatum(type, "class", UserData.SHARED);
		v0.addUserDatum(isAbstract, "false", UserData.SHARED);
		v1.addUserDatum(name, "V1", UserData.SHARED);
		v1.addUserDatum(type, "class", UserData.SHARED);
		v1.addUserDatum(isAbstract, "false", UserData.SHARED);
		v2.addUserDatum(name, "V2", UserData.SHARED);
		v2.addUserDatum(type, "class", UserData.SHARED);
		v2.addUserDatum(isAbstract, "true", UserData.SHARED);
		v3.addUserDatum(name, "V3", UserData.SHARED);
		v3.addUserDatum(type, "class", UserData.SHARED);
		v3.addUserDatum(isAbstract, "false", UserData.SHARED);
		
		//creating edges for graph
		Edge e0 = g.addEdge(new DirectedSparseEdge(v0, v1));
        Edge e1 = g.addEdge(new DirectedSparseEdge(v1, v2));
        Edge e2 = g.addEdge(new DirectedSparseEdge(v3, v1));
        //setting edges properties
        e0.addUserDatum(type, "uses", UserData.SHARED);
        e1.addUserDatum(type, "uses", UserData.SHARED);
        e2.addUserDatum(type, "extends", UserData.SHARED);
           
	}
	private Vertex getVertexFromGraph(String vertexname){
		String name ="name";
		Vertex testv = null;
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.getUserDatum(name).equals(vertexname)){
				testv = (Vertex) v;
			}
		}
		return testv;
	}
}
