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
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.GraphMLReader;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Tests for the graphml reader.
 * @author jens dietrich
 */

public class GraphMLReaderTests extends AbstractReaderTests {

	protected DirectedGraph<Vertex, Edge> loadGraph(String name) throws Exception {
        String src = "/test/nz/ac/massey/cs/gql4jung/io/data/"+name;
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(src));
        GraphMLReader greader = new GraphMLReader(reader);
        DirectedGraph<Vertex, Edge> g = greader.readGraph();
        greader.close();
        return g;
	}
	
	
	
	@Test
	public void testVertices1() throws Exception {
		DirectedGraph<Vertex, Edge> g = loadGraph("graph1.graphml");
		doTestVertices(
				"graph1.graphml",
				1,
				"1",
				"v1",
				"test1",
				true,
				"interface"
				);
	}
	@Test
	public void testVertices2() throws Exception {
		doTestVertices(
				"graph1.graphml",
				0,
				"1",
				"v1",
				"test1",
				false,
				"interface"
				);
	}
	@Test
	public void testVertices3() throws Exception {
		doTestVertices(
				"graph1.graphml",
				4,
				null,
				null,
				"test1",
				false,
				null
				);
	}
	@Test
	public void testVertices4() throws Exception {
		doTestVertices(
				"graph1.graphml",
				4,
				null,
				null,
				"test1",
				true,
				null
				);
	}
	
	@Test
	public void testEdges1() throws Exception {
		doTestEdges(
				"graph1.graphml",
				1,
				"edge-1-2",
				"1",
				"2",
				"uses"
				);
	}
	@Test
	public void testEdges2() throws Exception {
		doTestEdges(
				"graph1.graphml",
				0,
				"edge-1-2",
				"1",
				"2",
				"extends"
				);
	}
	@Test
	public void testEdges3() throws Exception {
		doTestEdges(
				"graph1.graphml",
				2,
				null,
				null,
				null,
				"extends"
				);
	}
	@Test
	public void testEdges4() throws Exception {
		doTestEdges(
				"graph1.graphml",
				7,
				null,
				null,
				null,
				null
				);
	}
	@Test
	public void testEdges5() throws Exception {
		doTestEdges(
				"graph1.graphml",
				2,
				null,
				"1",
				null,
				null
				);
	}
	
}
