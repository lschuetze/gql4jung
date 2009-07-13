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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.JarReader;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Tests for the depfind based jar reader.
 * @author jens dietrich
 */

public class JarReaderTests extends AbstractReaderTests {

	protected DirectedGraph<Vertex, Edge> loadGraph(String name) throws Exception {
        List<File> files = new ArrayList<File>();
        files.add(new File(name));
		JarReader greader = new JarReader(files);
        DirectedGraph<Vertex, Edge> g = greader.readGraph();
        greader.close();
        return g;
	}
	
	
	
	@Test
	public void testVertices1() throws Exception {
		doTestVertices(
				"testdata/test.jar",
				1,
				null,
				"Class1",
				"test",
				false,
				"class"
				);
	}
	

	@Test
	public void testEdges1() throws Exception {
		doTestEdges2(
				"testdata/test.jar",
				1,
				null,
				"test.Interface2",
				"test.Interface1",
				"extends"
				);
	}
	
	@Test
	public void testEdges2() throws Exception {
		doTestEdges2(
				"testdata/test.jar",
				1,
				null,
				"test.Class1",
				"test.Interface1",
				"implements"
				);
	}
	@Test
	public void testEdges3() throws Exception {
		doTestEdges2(
				"testdata/test.jar",
				1,
				null,
				"test.Class2",
				"test.Class1",
				"extends"
				);
	}
	@Test
	public void testEdges4() throws Exception {
		doTestEdges2(
				"testdata/test.jar",
				1,
				null,
				"test.Class3",
				"test.Class1",
				"uses"
				);
	}
	@Test
	public void testEdges5() throws Exception {
		doTestEdges2(
				"testdata/test.jar",
				1,
				null,
				"test.Class4",
				"test.Class1",
				"uses"
				);
	}
	@Test
	public void testEdges6() throws Exception {
		doTestEdges2(
				"testdata/test.jar",
				1,
				null,
				"test.Class5",
				"test.Class1",
				"uses"
				);
	}
	@Test
	public void testEdges7() throws Exception {
		doTestEdges2(
				"testdata/test.jar",
				1,
				null,
				"test.Class6",
				"test.Class1",
				"uses"
				);
	}
	@Test
	public void testEdges8() throws Exception {
		doTestEdges2(
				"testdata/test.jar",
				1,
				null,
				"test.Class7",
				"test.Class1",
				"uses"
				);
	}
	@Test
	public void testEdges9() throws Exception {
		doTestEdges2(
				"testdata/test.jar",
				1,
				null,
				"test.Class9",
				"test.Annotation1",
				"uses"
				);
	}

}
