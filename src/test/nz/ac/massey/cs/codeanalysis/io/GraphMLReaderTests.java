/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package test.nz.ac.massey.cs.codeanalysis.io;

import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.Test;
import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;
import nz.ac.massey.cs.codeanalysis.io.GraphMLReader;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Tests for the graphml reader.
 * @author jens dietrich
 */

public class GraphMLReaderTests extends AbstractReaderTests {

	protected DirectedGraph<TypeNode, TypeReference> loadGraph(String name) throws Exception {
        String src = "/test/nz/ac/massey/cs/codeanalysis/io/data/"+name;
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(src));
        GraphMLReader greader = new GraphMLReader(reader);
        DirectedGraph<TypeNode, TypeReference> g = greader.readGraph();
        greader.close();
        return g;
	}
	
	
	
	@Test
	public void testVertices1() throws Exception {
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
	public void testTypeReferences1() throws Exception {
		doTestTypeReferences(
				"graph1.graphml",
				1,
				"edge-1-2",
				"1",
				"2",
				"uses"
				);
	}
	@Test
	public void testTypeReferences2() throws Exception {
		doTestTypeReferences(
				"graph1.graphml",
				0,
				"edge-1-2",
				"1",
				"2",
				"extends"
				);
	}
	@Test
	public void testTypeReferences3() throws Exception {
		doTestTypeReferences(
				"graph1.graphml",
				2,
				null,
				null,
				null,
				"extends"
				);
	}
	@Test
	public void testTypeReferences4() throws Exception {
		doTestTypeReferences(
				"graph1.graphml",
				7,
				null,
				null,
				null,
				null
				);
	}
	@Test
	public void testTypeReferences5() throws Exception {
		doTestTypeReferences(
				"graph1.graphml",
				2,
				null,
				"1",
				null,
				null
				);
	}
	
}
