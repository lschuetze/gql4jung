package test.nz.ac.massey.cs.gql4jung.jmpl;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Tests for the new (jmpl) query engine implementation.
 * @author jens dietrich
 */

public class CDTests extends Tests{

	@Test
	public void test1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("inside1","org.example1.Inside1");
		expected.put("outside1","org.example2.Outside1");
		expected.put("outside2","org.example2.Outside2");
		expected.put("inside2","org.example1.Inside2");
		doTest("cd.xml","testdata-cd1.graphml",expected,true);
		doTestExpectedVariants("cd.xml","testdata-cd1.graphml",1);
	}
	
	@Test
	public void test2() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("inside1","org.example1.Inside1");
		expected.put("outside1","org.example2.Outside");
		expected.put("outside2","org.example2.Outside");
		expected.put("inside2","org.example1.Inside2");
		doTest("cd.xml","testdata-cd2.graphml",expected,true);
		doTestExpectedVariants("cd.xml","testdata-cd2.graphml",1);
	}
	
	@Test
	public void test3() throws Exception {
		doTestExpectedVariants("cd.xml","testdata-cd3.graphml",0);
	}
	
	@Test
	public void test4() throws Exception {
		doTestExpectedVariants("cd.xml","testdata-cd4.graphml",2);
		doTestExpectedInstances("cd.xml","testdata-cd4.graphml",2);
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
	public void testAnt2() throws Exception {
		doTestExpectedInstances("cd.xml","ant.jar.graphml",17);
	}
	
}
