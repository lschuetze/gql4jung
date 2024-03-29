/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package test.nz.ac.massey.cs.gql4jung.xml;

import java.io.*;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;
import org.junit.Test;

/**
 * Tests for XML schema.
 * @author jens.dietrich@gmail.com
 *
 */
public class ValidationTests {
	
	public static String SCHEMA = "schema/gql4jung.xsd";
	
	private Motif test(String name, boolean shouldFail) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new File(SCHEMA));
        Validator validator = schema.newValidator();
        
        String src = "/test/nz/ac/massey/cs/gql4jung/jmpl/queries/"+name;
        InputStream in = this.getClass().getResourceAsStream(src);
        
        Source source = new StreamSource(in);
        validator.validate(source);
        in.close();
        in = this.getClass().getResourceAsStream(src);
        return new XMLMotifReader().read(in);
 
	}
	
	@Test
	public void test1() throws Exception {
		Motif motif = test("awd.xml",false);
		System.out.println(motif);

	} 
	
	
	
} 

