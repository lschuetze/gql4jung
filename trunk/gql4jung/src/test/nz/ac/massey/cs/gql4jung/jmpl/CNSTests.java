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

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Tests for the new (jmpl) query engine implementation.
 * @author jens dietrich
 */

public class CNSTests extends Tests{

	@Test
	public void test1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("class1","org.example.Class1A");
		expected.put("class2","org.example.Class2A");
		doTest("cns.xml","testdata-cns1.graphml",expected,true);
	}
	
	
}
