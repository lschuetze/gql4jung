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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;
import nz.ac.massey.cs.codeanalysis.io.JarReader;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Tests for the depfind based jar reader.
 * @author jens dietrich
 */

public class JarReaderTests extends AbstractReaderTests {

	protected DirectedGraph<TypeNode, TypeReference> loadGraph(String name) throws Exception {
        List<File> files = new ArrayList<File>();
        files.add(new File(name));
		JarReader greader = new JarReader(files);
        DirectedGraph<TypeNode, TypeReference> g = greader.readGraph();
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
	public void testTypeReferences1() throws Exception {
		doTestTypeReferences2(
				"testdata/test.jar",
				1,
				null,
				"test.Interface2",
				"test.Interface1",
				"extends"
				);
	}
	
	@Test
	public void testTypeReferences2() throws Exception {
		doTestTypeReferences2(
				"testdata/test.jar",
				1,
				null,
				"test.Class1",
				"test.Interface1",
				"implements"
				);
	}
	@Test
	public void testTypeReferences3() throws Exception {
		doTestTypeReferences2(
				"testdata/test.jar",
				1,
				null,
				"test.Class2",
				"test.Class1",
				"extends"
				);
	}
	@Test
	public void testTypeReferences4() throws Exception {
		doTestTypeReferences2(
				"testdata/test.jar",
				1,
				null,
				"test.Class3",
				"test.Class1",
				"uses"
				);
	}
	@Test
	public void testTypeReferences5() throws Exception {
		doTestTypeReferences2(
				"testdata/test.jar",
				1,
				null,
				"test.Class4",
				"test.Class1",
				"uses"
				);
	}
	@Test
	public void testTypeReferences6() throws Exception {
		doTestTypeReferences2(
				"testdata/test.jar",
				1,
				null,
				"test.Class5",
				"test.Class1",
				"uses"
				);
	}
	@Test
	public void testTypeReferences7() throws Exception {
		doTestTypeReferences2(
				"testdata/test.jar",
				1,
				null,
				"test.Class6",
				"test.Class1",
				"uses"
				);
	}
	@Test
	public void testTypeReferences8() throws Exception {
		doTestTypeReferences2(
				"testdata/test.jar",
				1,
				null,
				"test.Class7",
				"test.Class1",
				"uses"
				);
	}
	@Test
	public void testTypeReferences9() throws Exception {
		doTestTypeReferences2(
				"testdata/test.jar",
				1,
				null,
				"test.Class9",
				"test.Annotation1",
				"uses"
				);
	}

}
