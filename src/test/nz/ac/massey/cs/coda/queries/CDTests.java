/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package test.nz.ac.massey.cs.coda.queries;

import java.util.HashMap;
import java.util.Map;

import nz.ac.massey.cs.coda.TypeNode;
import nz.ac.massey.cs.coda.TypeReference;
import nz.ac.massey.cs.gql4jung.GQL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests for the new (jmpl) query engine implementation.
 * @author jens dietrich
 */

@RunWith(Parameterized.class)
public class CDTests extends Tests{

	public CDTests(GQL<TypeNode,TypeReference> engine) {
		super(engine);
	}

	@Test
	public void test1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("inside1","org.example1.Inside1");
		expected.put("outside1","org.example2.Outside1");
		expected.put("outside2","org.example2.Outside2");
		expected.put("inside2","org.example1.Inside2");
		doTest("cd.xml","testdata-cd1.graphml",expected,true);
		doTestExpectedVariants("cd.xml","testdata-cd1.graphml",1,false);
	}
	
	@Test
	public void test2() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("inside1","org.example1.Inside1");
		expected.put("outside1","org.example2.Outside");
		expected.put("outside2","org.example2.Outside");
		expected.put("inside2","org.example1.Inside2");
		doTest("cd.xml","testdata-cd2.graphml",expected,true);
		doTestExpectedVariants("cd.xml","testdata-cd2.graphml",1,false);
	}
	
	@Test
	public void test3() throws Exception {
		doTestExpectedVariants("cd.xml","testdata-cd3.graphml",0,false);
	}
	
	@Test
	public void test4() throws Exception {
		doTestExpectedVariants("cd.xml","testdata-cd4.graphml",2,false);
		doTestExpectedInstances("cd.xml","testdata-cd4.graphml",2,true);
		doTestExpectedInstances("cd.xml","testdata-cd4.graphml",2,false);
	}
	
	@Test
	public void testAnt1() throws Exception {	
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("inside1","org.apache.tools.ant.input.PropertyFileInputHandler");
		expected.put("outside1","org.apache.tools.ant.BuildException");
		expected.put("outside2","org.apache.tools.ant.Project");
		expected.put("inside2","org.apache.tools.ant.input.InputHandler");
		doTest("cd.xml","ant.jar.graphml",expected,true);
	}
	/**
	 * The number of instances found in 0.2 and o3 is the same, they have been manually verified.
	 * The number of variants has slightly increased.
	 * @throws Exception
	 */
	@Test
	public void testAnt2IgnoreVariants() throws Exception {
		doTestExpectedInstances("cd.xml","ant.jar.graphml",17,true);
	}
	/**
	 * The number of instances found in 0.2 and o3 is the same, they have been manually verified.
	 * The number of variants has slightly increased.
	 * @throws Exception
	 */
	@Test
	public void testAnt3ComputeVariants() throws Exception {
		doTestExpectedInstances("cd.xml","ant.jar.graphml",17,false);
	}
	
	@Test
	public void testVariants1() throws Exception {
		doTestExpectedInstances("cd.xml","testdata-cd5.graphml",1,true);
	}
	@Test
	public void testVariants2() throws Exception {
		doTestExpectedInstances("cd.xml","testdata-cd5.graphml",1,false);
	}
	@Test
	public void testVariants3() throws Exception {
		doTestExpectedVariants("cd.xml","testdata-cd5.graphml",2,false);
	}
	@Test
	public void testVariants4() throws Exception {
		doTestExpectedVariants("cd.xml","testdata-cd5.graphml",1,true);
	}
	
}
