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
public class CNSTests extends Tests{

	
	public CNSTests(GQL<TypeNode,TypeReference> engine) {
		super(engine);
	}

	@Test
	public void test1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("class1","org.example.Class1A");
		expected.put("class2","org.example.Class2A");
		doTest("cns.xml","testdata-cns1.graphml",expected,true);
	}
	@Test
	public void test2() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("class1","org.example1.Class1A");
		expected.put("class2","org.example1.Class1B");
		doTest("cns.xml","testdata-cns2.graphml",expected,true);
	}
	
	@Test
	public void test3() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("class1","org.example2.Class2A");
		expected.put("class2","org.example2.Class2B");
		doTest("cns.xml","testdata-cns2.graphml",expected,true);
	}
	
	@Test
	public void test4() throws Exception {
		this.doTestExpectedInstances("cns.xml","testdata-cns2.graphml", 2, true);
	}
	
	
	
}
