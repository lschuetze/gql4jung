/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package test.nz.ac.massey.cs.gql4jung.io;

import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.Test;

import nz.ac.massey.cs.codeanalysis.ODEMReader;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Tests for the graphml reader.
 * @author jens dietrich
 */

public class ODEMReaderTests extends AbstractReaderTests {

	protected DirectedGraph<Vertex, Edge> loadGraph(String name) throws Exception {
        String src = "/test/nz/ac/massey/cs/gql4jung/io/data/"+name;
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(src));
        ODEMReader greader = new ODEMReader(reader);
        DirectedGraph<Vertex, Edge> g = greader.readGraph();
        greader.close();
        return g;
	}
	
	
	@Test
	public void testVertices1() throws Exception {
		doTestVertices(
				"small.odem",
				5,
				null,
				null,
				null,
				false,
				null
				);
	}
	
	@Test
	public void testVertices2() throws Exception {
		doTestVertices(
				"small.odem",
				4,
				null,
				null,
				null,
				false,
				"class"  // outside class java.lang.Object is not classified
				);
	}
	
	@Test
	public void testVertices3() throws Exception {
		doTestVertices(
				"small.odem",
				3,
				null,
				null,
				"com.example1",
				false,
				"class"
				);
	}
	
	@Test
	public void testVertices4() throws Exception {
		doTestVertices(
				"small.odem",
				1,
				null,
				"Class11",
				"com.example1",
				false,
				"class"
				);
	}
	// test for referenced outside class
	@Test
	public void testVertices5() throws Exception {
		doTestVertices(
				"small.odem",
				1,
				null,
				"Object",
				"java.lang",
				false,
				null
				);
	}
	// test how ids are assigned
	@Test
	public void testVertices6() throws Exception {
		doTestVertices(
				"small.odem",
				1,
				"v0",
				"Class11",
				"com.example1",
				false,
				"class"
				);
	}
	
	@Test
	public void testEdges1() throws Exception {
		doTestEdges(
				"small.odem",
				7,
				null,
				null,
				null,
				null
				);
	}
	
	@Test
	public void testEdges2() throws Exception {
		doTestEdges(
				"small.odem",
				4,
				null,
				null,
				null,
				"extends"
				);
	}
	
	@Test
	public void testEdges3() throws Exception {
		doTestEdges(
				"small.odem",
				3,
				null,
				null,
				null,
				"uses"
				);
	}
	
	@Test
	public void testEdges4() throws Exception {
		doTestEdges(
				"small.odem",
				0,
				null,
				"v0",
				"v1",
				"uses"
				);
	}
	
}
