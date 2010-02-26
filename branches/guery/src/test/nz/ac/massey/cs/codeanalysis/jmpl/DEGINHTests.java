/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package test.nz.ac.massey.cs.codeanalysis.jmpl;

import java.util.HashMap;
import java.util.Map;

import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;
import nz.ac.massey.cs.gql4jung.GQL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests for the new (jmpl) query engine implementation.
 * @author jens dietrich
 */

@RunWith(Parameterized.class)
public class DEGINHTests extends Tests{

	public DEGINHTests(GQL<TypeNode,TypeReference> engine) {
		super(engine);
	}

	@Test
	public void test1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("type","org.example1.Class");
		expected.put("supertype","org.example1.Interface");
		doTest("deginh.xml","testdata-deginh1.graphml",expected,true);
	}
	
	@Test
	public void test2() throws Exception {
		this.doTestExpectedInstances("deginh.xml","testdata-deginh1.graphml", 1,false);
	}
	
}