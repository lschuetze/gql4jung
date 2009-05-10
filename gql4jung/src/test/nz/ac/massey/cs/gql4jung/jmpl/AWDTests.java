package test.nz.ac.massey.cs.gql4jung.jmpl;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 * Tests for the new (jmpl) query engine implementation.
 * @author jens dietrich
 */

public class AWDTests extends Tests{


	@Test
	public void testAnt1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.apache.tools.ant.filters.TokenFilter");
		expected.put("service","org.apache.tools.ant.filters.TokenFilter$ChainableReaderFilter");
		expected.put("service_impl","org.apache.tools.ant.filters.TokenFilter$ContainsRegex");		
		doTest("awd.xml","ant.jar.graphml",expected,true);
	}
	@Test
	public void testAnt2() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.apache.tools.ant.filters.util.ChainReaderHelper");
		expected.put("service","org.apache.tools.ant.filters.BaseFilterReader");
		expected.put("service_impl","org.apache.tools.ant.filters.LineContains");		
		doTest("awd.xml","ant.jar.graphml",expected,true);
	}
	@Test
	public void test1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.example.AClient");
		expected.put("service","org.example.AService");
		expected.put("service_impl","org.example.AServiceImpl");		
		doTest("awd.xml","testdata-awd1.graphml",expected,true);
	}
	@Test
	public void test2() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.example.AClient");
		expected.put("service","org.example.AService");
		expected.put("service_impl","org.example.AServiceImpl");		
		doTest("awd.xml","testdata-awd2.graphml",expected,true);
	}
	@Test
	public void test3() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.example.AClient");
		expected.put("service","org.example.AService");
		expected.put("service_impl","org.example.AServiceImpl");		
		doTest("awd.xml","testdata-awd3.graphml",expected,true);
	}
	@Test
	public void test4() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.example.AClient");
		expected.put("service","org.example.AService");
		expected.put("service_impl","org.example.AServiceImpl");		
		doTest("awd.xml","testdata-awd4.graphml",expected,true);
	}
	@Test
	public void test5() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("client","org.example.AClient");
		expected.put("service","org.example.AService");
		expected.put("service_impl","org.example.AServiceImpl");		
		doTest("awd.xml","testdata-awd5.graphml",expected,true);
	}
	
}
