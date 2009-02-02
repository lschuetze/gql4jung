package test.nz.ac.massey.cs.gpl4jung.gql;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.gpl4jung.PropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.NegatedPropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.Operator;
import nz.ac.massey.cs.gpl4jung.constraints.PropertyConstraintConjunction;
import nz.ac.massey.cs.gpl4jung.constraints.PropertyConstraintDisjunction;
import nz.ac.massey.cs.gpl4jung.constraints.PropertyTerm;
import nz.ac.massey.cs.gpl4jung.constraints.SimplePropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.ValueTerm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;

public class ComplexPropertyConstraintTests {
	static Graph g = null; 
	@Before
	public void setUp() throws Exception {
		g = new DirectedSparseGraph();	
		buildGraph();
	}
	
	@After
	public void tearDown() throws Exception {
		g=null;
	}
	@Test
	public void testCheckNegated(){
		//test for Negated propertycondition
		SimplePropertyConstraint<Vertex> vc = new SimplePropertyConstraint<Vertex>();
		NegatedPropertyConstraint<Vertex> npc = new NegatedPropertyConstraint<Vertex>();
		PropertyTerm term1 = new PropertyTerm("type");
		ValueTerm term2 = new ValueTerm("package");
		Operator op = Operator.getInstance("=");
		vc.setOperator(op);
		vc.setTerms(term1,term2);
		npc.setPart(vc);
		Vertex testV1 = getVertexFromGraph("V1");
		assertFalse(npc.check(g, testV1));
	}
	@Test
	//test for complex property condition using OR
	public void testPropertyConstraintDisjunction(){
		Edge testEdge = getEdgeFromGraph("E2");
		List<PropertyConstraint<Edge>> propertyParts = new ArrayList<PropertyConstraint<Edge>>();
		SimplePropertyConstraint<Edge> part1 = new SimplePropertyConstraint<Edge>();
		PropertyConstraintDisjunction<Edge> or = new PropertyConstraintDisjunction<Edge>();
		//getting 1st propertyconstraint
		PropertyTerm term1 = new PropertyTerm("type");
		ValueTerm term2 = new ValueTerm("extends");
		Operator op = Operator.getInstance("=");
		part1.setTerms(term1,term2);
		part1.setOperator(op);
		propertyParts.add(part1);
		//getting 2nd propertyconstraint
		SimplePropertyConstraint<Edge> part2 = new SimplePropertyConstraint<Edge>();
		PropertyTerm term3 = new PropertyTerm("type");
		ValueTerm term4 = new ValueTerm("uses");
		Operator op1 = Operator.getInstance("=");
		part2.setTerms(term3,term4);
		part2.setOperator(op1);
		propertyParts.add(part2);
		//checking complexpropertyconstraint OR
		or.setParts(propertyParts);
		assertTrue(or.check(g, testEdge));
	}
	@Test
	//test for complex property condition using AND
	public void testPropertyConstraintConjunction(){
		Vertex testVertex = getVertexFromGraph("V1");
		List<PropertyConstraint<Vertex>> propertyParts = new ArrayList<PropertyConstraint<Vertex>>();
		SimplePropertyConstraint<Vertex> part1 = new SimplePropertyConstraint<Vertex>();
		PropertyConstraintConjunction<Vertex> and = new PropertyConstraintConjunction<Vertex>();
		//getting 1st propertyconstraint
		PropertyTerm term1 = new PropertyTerm("type");
		ValueTerm term2 = new ValueTerm("package");
		Operator op = Operator.getInstance("=");
		part1.setTerms(term1,term2);
		part1.setOperator(op);
		propertyParts.add(part1);
		//getting 2nd propertyconstraint
		SimplePropertyConstraint<Vertex> part2 = new SimplePropertyConstraint<Vertex>();
		PropertyTerm term3 = new PropertyTerm("isAbstract");
		ValueTerm term4 = new ValueTerm("false");
		Operator op1 = Operator.getInstance("=");
		part2.setTerms(term3,term4);
		part2.setOperator(op1);
		propertyParts.add(part2);
		//checking complexpropertyconstraint AND
		and.setParts(propertyParts);
		assertTrue(and.check(g, testVertex));
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
		v1.addUserDatum(type, "package", UserData.SHARED);
		v1.addUserDatum(isAbstract, "false", UserData.SHARED);
		v2.addUserDatum(name, "V2", UserData.SHARED);
		v2.addUserDatum(type, "class", UserData.SHARED);
		v2.addUserDatum(isAbstract, "true", UserData.SHARED);
		v3.addUserDatum(name, "V3", UserData.SHARED);
		v3.addUserDatum(type, "java.util.regex.Pattern", UserData.SHARED);
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
}
