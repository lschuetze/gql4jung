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
public class DB2UITests extends Tests{

	public DB2UITests(GQL<TypeNode,TypeReference> engine) {
		super(engine);
	}

	@Test
	public void test1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","java.sql.Driver");
		expected.put("uiclass","javax.swing.JFrame");
		expected.put("dblayerclass","org.example1.MyDB");
		expected.put("uilayerclass","org.example1.MyFrame");
		doTest("db2ui.xml","testdata-db2ui1.graphml",expected,true);
		doTestExpectedVariants("db2ui.xml","testdata-db2ui1.graphml",1,false);
	}
	
	@Test
	public void test() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","org.hibernate.Transaction");
		expected.put("uiclass","java.awt.Frame");
		expected.put("dblayerclass","org.example1.MyDB");
		expected.put("uilayerclass","org.example1.MyFrame");
		doTest("db2ui.xml","testdata-db2ui2.graphml",expected,true);
		doTestExpectedVariants("db2ui.xml","testdata-db2ui2.graphml",1,false);
	}
	
	@Test
	public void test3() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","java.sql.Driver");
		expected.put("uiclass","javax.swing.JFrame");
		expected.put("dblayerclass","org.example1.MyDB");
		expected.put("uilayerclass","org.example1.MyFrame");
		doTest("db2ui.xml","testdata-db2ui3.graphml",expected,true);
		doTestExpectedVariants("db2ui.xml","testdata-db2ui3.graphml",1,false);
	}
	
	@Test
	public void test4() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","java.sql.Driver");
		expected.put("uiclass","javax.swing.JFrame");
		expected.put("dblayerclass","org.example1.MyClass");
		expected.put("uilayerclass","org.example1.MyClass");
		doTest("db2ui.xml","testdata-db2ui4.graphml",expected,true);
		doTestExpectedVariants("db2ui.xml","testdata-db2ui4.graphml",1,false);
	}
	
	@Test
	public void testjt400Proxy1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","java.sql.ParameterMetaData");
		expected.put("uiclass","java.awt.Container");
		expected.put("dblayerclass","com.ibm.as400.access.JDParameterMetaDataProxy");
		expected.put("uilayerclass","com.ibm.as400.access.PasswordDialog");
		doTest("db2ui.xml","jt400Proxy.jar.graphml",expected,true);
	}
	/**
	 * Number of instances unchanged from 0.2 o 0.3, one instance manually verified.
	 * @throws Exception
	 */
	@Test
	public void testjt400ProxyCountAllComputeAllVariants() throws Exception {
		this.doTestExpectedInstances("db2ui.xml", "jt400Proxy.jar.graphml", 1,false);
	}
	/**
	 * Number of instances unchanged from 0.2 o 0.3, one instance manually verified.
	 * @throws Exception
	 */
	@Test
	public void testjt400ProxyCountAllIgnoreVariants() throws Exception {
		this.doTestExpectedInstances("db2ui.xml", "jt400Proxy.jar.graphml", 1,true);
	}
	/**
	 * Number of variants unchanged from 0.2 o 0.3, one instance manually verified.
	 * @throws Exception
	 */
	@Test
	public void testjt400Proxy3() throws Exception {
		this.doTestExpectedVariants("db2ui.xml", "jt400Proxy.jar.graphml", 4103,false);
	}
}
