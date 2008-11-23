/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package test.nz.ac.massey.cs.gpl4jung.xml;

import java.io.*;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for XML schema.
 * @author jens.dietrich@gmail.com
 *
 */
public class ValidationTests {
	
	public static String SCHEMA = "xml/gql4jung.xsd";
	
	private void test(String xml, boolean shouldFail) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new File(SCHEMA));
        Validator validator = schema.newValidator();
        Source source = new StreamSource(new File(xml));
        try {
        	validator.validate(source);
        	Assert.assertTrue(!shouldFail);
        }
        catch (Throwable t) {
        	t.printStackTrace();
        	Assert.assertTrue(shouldFail);
        	
        }
	}
	
	@Test
	public void test1() throws Exception {
		test("xml/query1.xml",false);

	} 
	
	@Test
	public void test2() throws Exception {
		test("xml/query2.xml",false);

	} 
	
	@Test
	public void test3() throws Exception {
		test("xml/query3.xml",false);

	} 
	
	@Test
	public void test4() throws Exception {
		test("xml/query4.xml",false);
	}
	
	@Test
	public void test_empty_query() throws Exception {
		test("xml/testdata/test_empty_query.xml",false);		
	}
	
	@Test
	public void test_query1_1() throws Exception {
		test("xml/testdata/test_query1_1.xml",false);
	}
	
	@Test
	public void test_query1_2() throws Exception {
		test("xml/testdata/test_query1_2.xml",false);	
	}
	
	@Test
	public void test_query1_3() throws Exception {			
		test("xml/testdata/test_query1_3.xml",false);
	}

	@Test
	public void test_query1_4() throws Exception {
		test("xml/testdata/test_query1_4.xml",false);	
	}

	@Test
	public void test_query1_5() throws Exception {
		test("xml/testdata/test_query1_5.xml",false);		
	}
	
	@Test
	public void test_query2_1() throws Exception {
		test("xml/testdata/test_query2_1.xml",false);
	}
	
	@Test
	public void test_query3_1() throws Exception {
		test("xml/testdata/test_query3_1.xml",false);	
	}
	
	@Test
	public void test_query3_2() throws Exception {			
		test("xml/testdata/test_query3_2.xml",false);
	}

	@Test
	public void test_query4_1() throws Exception {
		test("xml/testdata/test_query4_1.xml",false);	
	}
	
	
} 

