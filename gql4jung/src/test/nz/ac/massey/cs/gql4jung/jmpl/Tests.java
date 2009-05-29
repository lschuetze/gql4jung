/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package test.nz.ac.massey.cs.gql4jung.jmpl;

import static junit.framework.Assert.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.GraphMLReader;
import nz.ac.massey.cs.gql4jung.jmpl.GQLImpl;
import nz.ac.massey.cs.gql4jung.util.QueryResults;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Abstract superclass for tests for the new (jmpl) query engine implementation.
 * @author jens dietrich
 */

public abstract class Tests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	static DirectedGraph<Vertex, Edge> loadGraph(String name) throws Exception {
        String src = "/test/nz/ac/massey/cs/gql4jung/jmpl/data/"+name;
        Reader reader = new InputStreamReader(Tests.class.getResourceAsStream(src));
        GraphMLReader greader = new GraphMLReader(reader);
        DirectedGraph<Vertex, Edge> g = greader.readGraph();
        greader.close();
        return g;
	}
	static Motif loadQuery(String name) throws Exception {
		String src = "/test/nz/ac/massey/cs/gql4jung/jmpl/queries/"+name;
        return new XMLMotifReader().read(Tests.class.getResourceAsStream(src));
	}
	// expected associates roles with full class names
	protected boolean check(List<MotifInstance> instances,Map<String,String> expected) {
		for (MotifInstance inst:instances) {
			boolean result = true;
			for (String role:expected.keySet()) {
				Vertex v = inst.getVertex(role);
				if (result) {
					String classname = ""+v.getNamespace()+'.'+v.getName();
					result = result && classname.equals(expected.get(role));
				}
			}
			if (result) return true;
		}
		return false;
	} 
	// check whether a result specified as map is or is not in the result set
	protected void doTest(String motif, String data,Map<String, String> expected,boolean shouldSucceed) throws Exception {
		DirectedGraph<Vertex,Edge> g = this.loadGraph(data);
		Motif m = this.loadQuery(motif);
		ResultCollector coll = new ResultCollector();
		GQL engine = new GQLImpl();
		long t1 = System.currentTimeMillis();
		engine.query(g,m,coll);
		long t2 = System.currentTimeMillis();
		System.out.println("query "+motif+" on data "+data+ " returned "+coll.getInstances().size()+" results");
		System.out.println("query "+motif+" on data "+data+ " took "+(t2-t1)+" millis");
		assertTrue(shouldSucceed==check(coll.getInstances(),expected));
	}
	
	
	// check the expected number of variants (different results, no aggregation)
	protected void doTestExpectedVariants(String motif, String data,int expected) throws Exception {
		DirectedGraph<Vertex,Edge> g = this.loadGraph(data);
		Motif m = this.loadQuery(motif);
		ResultCollector coll = new ResultCollector();
		GQL engine = new GQLImpl();
		long t1 = System.currentTimeMillis();
		engine.query(g,m,coll);
		long t2 = System.currentTimeMillis();
		System.out.println("query "+motif+" on data "+data+ " returned "+coll.getInstances().size()+" variants");
		System.out.println("query "+motif+" on data "+data+ " took "+(t2-t1)+" millis");
		assertEquals(expected,coll.getInstances().size());
	}
	// check the expected number of variants (different results, no aggregation)
	protected void doTestExpectedInstances(String motif, String data,int expected) throws Exception {
		DirectedGraph<Vertex,Edge> g = this.loadGraph(data);
		Motif m = this.loadQuery(motif);
		QueryResults coll = new QueryResults();
		GQL engine = new GQLImpl();
		long t1 = System.currentTimeMillis();
		engine.query(g,m,coll);
		long t2 = System.currentTimeMillis();
		System.out.println("query "+motif+" on data "+data+ " returned "+coll.getNumberOfGroups()+" instances");
		System.out.println("query "+motif+" on data "+data+ " took "+(t2-t1)+" millis");
		assertEquals(expected,coll.getNumberOfGroups());
	}
}
