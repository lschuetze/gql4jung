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
