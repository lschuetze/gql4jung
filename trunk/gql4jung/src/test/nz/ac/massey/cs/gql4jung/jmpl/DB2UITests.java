package test.nz.ac.massey.cs.gql4jung.jmpl;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Tests for the new (jmpl) query engine implementation.
 * @author jens dietrich
 */

public class DB2UITests extends Tests{

	@Test
	public void test1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","java.sql.Driver");
		expected.put("uiclass","javax.swing.JFrame");
		expected.put("dblayerclass","org.example1.MyDB");
		expected.put("uilayerclass","org.example1.MyFrame");
		doTest("db2ui.xml","testdata-db2ui1.graphml",expected,true);
		doTestExpectedVariants("db2ui.xml","testdata-db2ui1.graphml",1);
	}
	
	@Test
	public void test() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","org.hibernate.Transaction");
		expected.put("uiclass","java.awt.Frame");
		expected.put("dblayerclass","org.example1.MyDB");
		expected.put("uilayerclass","org.example1.MyFrame");
		doTest("db2ui.xml","testdata-db2ui2.graphml",expected,true);
		doTestExpectedVariants("db2ui.xml","testdata-db2ui2.graphml",1);
	}
	
	@Test
	public void test3() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","java.sql.Driver");
		expected.put("uiclass","javax.swing.JFrame");
		expected.put("dblayerclass","org.example1.MyDB");
		expected.put("uilayerclass","org.example1.MyFrame");
		doTest("db2ui.xml","testdata-db2ui3.graphml",expected,true);
		doTestExpectedVariants("db2ui.xml","testdata-db2ui3.graphml",1);
	}
	
	@Test
	public void test4() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","java.sql.Driver");
		expected.put("uiclass","javax.swing.JFrame");
		expected.put("dblayerclass","org.example1.MyClass");
		expected.put("uilayerclass","org.example1.MyClass");
		doTest("db2ui.xml","testdata-db2ui4.graphml",expected,true);
		doTestExpectedVariants("db2ui.xml","testdata-db2ui4.graphml",1);
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
}
