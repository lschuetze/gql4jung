package test.nz.ac.massey.cs.gql4jung.gql;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.gql4jung.constraints.Operator;
import nz.ac.massey.cs.gql4jung.constraints.PropertyTerm;
import nz.ac.massey.cs.gql4jung.constraints.SimplePropertyConstraint;
import nz.ac.massey.cs.gql4jung.constraints.ValueTerm;


import org.junit.After;
import org.junit.Before;
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

public class PropertyConstraintTests {
	static Graph g = null; 
	@Before 
	public void setUp()throws Exception {
		g = new DirectedSparseGraph();	
		buildGraph();
	}
	@After
	public void tearDown()throws Exception{
		g=null;
	}
	@Test
	public void testCheckVertices() {
		SimplePropertyConstraint<Vertex> vc = new SimplePropertyConstraint<Vertex>();
		PropertyTerm term1 = new PropertyTerm("isAbstract");
		ValueTerm term2 = new ValueTerm("false");
		Operator op = Operator.getInstance("=");
		vc.setOperator(op);
		vc.setTerms(term1,term2);
		Vertex testV1 = getVertexFromGraph("V1");
		Vertex testV0 = getVertexFromGraph("V0");
		Vertex testV3 = getVertexFromGraph("V3");
		assertTrue(vc.check(g, testV0, testV1, testV3));
		//test for NOT EQUAL OPERATOR for the above test scenario '!='
		op = Operator.getInstance("!=");
		vc.setOperator(op);
		Object x = term1.getValue(testV1);
		System.out.println(x);
		assertFalse(vc.check(g,testV0, testV1, testV3));
		
	}
	@Test
	public void testCheckEdges() {
		SimplePropertyConstraint<Edge> ec = new SimplePropertyConstraint<Edge>();
		PropertyTerm term1 = new PropertyTerm("type");
		ValueTerm term2 = new ValueTerm("uses");
		Operator op = Operator.getInstance("=");
		ec.setOperator(op);
		ec.setTerms(term1,term2);
		Edge testE1 = getEdgeFromGraph("E0");
		Edge testE2 = getEdgeFromGraph("E1");
		assertTrue(ec.check(g, testE1, testE2));
		//test for NOT EQUAL OPERATOR for the above test scenario '!='
		op = Operator.getInstance("!=");
		ec.setOperator(op);
		assertFalse(ec.check(g, testE1, testE2));
	}
	@Test
	public void testCheckPath(){
		SimplePropertyConstraint<Edge> ec = new SimplePropertyConstraint<Edge>();
		Vertex source = getVertexFromGraph("V0");
		Vertex target = getVertexFromGraph("V2");
		Edge[] path = calcShortestPath(source, target);
		PropertyTerm term1 = new PropertyTerm("type");
		ValueTerm term2 = new ValueTerm("uses");
		Operator op = Operator.getInstance("=");
		ec.setOperator(op);
		ec.setTerms(term1,term2);
		assertTrue(ec.check(g, path));
	}
	@Test
	//to test 'IN' operator 
	public void testCheckIN() {
		SimplePropertyConstraint<Vertex> vc = new SimplePropertyConstraint<Vertex>();
		PropertyTerm term1 = new PropertyTerm("type");
		ValueTerm term2 = new ValueTerm("my class,package");
		Operator op = Operator.getInstance("IN");
		vc.setOperator(op);
		vc.setTerms(term1,term2);
		Vertex testV0 = getVertexFromGraph("V0");
		assertTrue(vc.check(g, testV0));
	}
	
	@Test
	//to test REGEX operator
	public void testCheckREGEX(){
		SimplePropertyConstraint<Vertex> vc = new SimplePropertyConstraint<Vertex>();
		PropertyTerm term1 = new PropertyTerm("type");
		ValueTerm term2 = new ValueTerm(".*javax\\.swing.*");
		Operator op = Operator.getInstance("matches");
		vc.setOperator(op);
		vc.setTerms(term1,term2);
		Vertex testV3 = getVertexFromGraph("V3");
		assertTrue(vc.check(g, testV3));
	}
	@Test
	//to test REGEX for multiple values on a path of edges e.g. 'uses' or 'extends
	public void testCheckREGEX1(){
		SimplePropertyConstraint<Edge> ec = new SimplePropertyConstraint<Edge>();
		Vertex source = getVertexFromGraph("V3");
		Vertex target = getVertexFromGraph("V2");
		Edge[] path = calcShortestPath(source, target);
		PropertyTerm term1 = new PropertyTerm("type");
		ValueTerm term2 = new ValueTerm("u.*|e.*");
		Operator op = Operator.getInstance("matches");
		ec.setOperator(op);
		ec.setTerms(term1,term2);
		assertTrue(ec.check(g, path));
	}
	@Test
	//to test for greater than '>' operator 
	//returns true if the value at node/edge is greater than the value we provide, false otherwise
	public void testCheckGT(){
		SimplePropertyConstraint<Vertex> vc = new SimplePropertyConstraint<Vertex>();
		PropertyTerm term1 = new PropertyTerm("count");
		ValueTerm term2 = new ValueTerm("41");
		Operator op = Operator.getInstance("gt");
		vc.setOperator(op);
		vc.setTerms(term1,term2);
		Vertex testV3 = getVertexFromGraph("V3");
		assertTrue(vc.check(g, testV3));
	}
	@Test
	//to test for greater than '>' operator 
	//when we enter fortyone instead of 41. it should fail
	public void testCheckGT1(){
		SimplePropertyConstraint<Vertex> vc = new SimplePropertyConstraint<Vertex>();
		PropertyTerm term1 = new PropertyTerm("count");
		ValueTerm term2 = new ValueTerm("fortyone");
		Operator op = Operator.getInstance("gt");
		vc.setOperator(op);
		vc.setTerms(term1,term2);
		Vertex testV3 = getVertexFromGraph("V3");
		assert(vc.check(g,testV3));
	}
	private static void buildGraph(){
		//keys definition
		String name = "name";
		String type = "type";
		String isAbstract="isAbstract";
		String count = "count";
		
		//creating vertices for graph
		Vertex v0 = g.addVertex(new DirectedSparseVertex());
		Vertex v1 = g.addVertex(new DirectedSparseVertex());
		Vertex v2 = g.addVertex(new DirectedSparseVertex());
		Vertex v3 = g.addVertex(new DirectedSparseVertex());
		
		//setting up vertices names and other properties e.g. (type, isAbstract etc)
		v0.addUserDatum(name, "V0", UserData.SHARED);
		v0.addUserDatum(type, "my class", UserData.SHARED);
		v0.addUserDatum(isAbstract, "false", UserData.SHARED);
		v1.addUserDatum(name, "V1", UserData.SHARED);
		v1.addUserDatum(type, "class", UserData.SHARED);
		v1.addUserDatum(isAbstract, "false", UserData.SHARED);
		v2.addUserDatum(name, "V2", UserData.SHARED);
		v2.addUserDatum(type, "class", UserData.SHARED);
		v2.addUserDatum(isAbstract, "true", UserData.SHARED);
		v3.addUserDatum(name, "V3", UserData.SHARED);
		v3.addUserDatum(type, "java.lang.Object|javax.swing.JOption|javax.swing.JPanel", UserData.SHARED);
		v3.addUserDatum(isAbstract, "false", UserData.SHARED);
		v3.addUserDatum(count, "42", UserData.SHARED);
		//creating edges for graph
		Edge e0 = g.addEdge(new DirectedSparseEdge(v0, v1));
        Edge e1 = g.addEdge(new DirectedSparseEdge(v1, v2));
        Edge e2 = g.addEdge(new DirectedSparseEdge(v3, v1));
        //setting edges properties
        e0.addUserDatum(name, "E0", UserData.SHARED);
        e0.addUserDatum(type, "uses", UserData.SHARED);
        e1.addUserDatum(name, "E1", UserData.SHARED);
        e1.addUserDatum(type, "uses", UserData.SHARED);
        e2.addUserDatum(name, "E2", UserData.SHARED);
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

	private Edge getEdgeFromGraph(String edgename){
		String name ="name";
		Edge testedge = null;
		for (Iterator iter = g.getEdges().iterator(); iter.hasNext();){
			Edge e = (Edge) iter.next();
			if(e.getUserDatum(name).equals(edgename)){
				testedge = (Edge) e;
			}
		}
		return testedge;
	}
	
	private Edge[] calcShortestPath(Vertex source, Vertex target){
		ShortestPath SPA = new DijkstraShortestPath(g);
		List pathlist = ShortestPathUtils.getPath(SPA,source,target);
		Edge path[] = new Edge[pathlist.size()];
		for(Iterator itr=pathlist.iterator();itr.hasNext();){
			for(int i=0;i<pathlist.size();i++){
				Edge e = (Edge) itr.next();
				path[i]= e;
			}
		}
		return path;
	}
}
