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
	public void testCD1() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","java.sql.Driver");
		expected.put("uiclass","javax.swing.JFrame");
		expected.put("dblayerclass","org.example1.MyDB");
		expected.put("uilayerclass","org.example1.MyFrame");
		doTest("db2ui.xml","testdata-db2ui1.graphml",expected,true);
		doTest("db2ui.xml","testdata-db2ui1.graphml",1);
	}
	
	@Test
	public void testCD2() throws Exception {
		Map<String,String> expected = new HashMap<String,String>();
		expected.put("dbclass","org.hibernate.Transaction");
		expected.put("uiclass","java.awt.Frame");
		expected.put("dblayerclass","org.example1.MyDB");
		expected.put("uilayerclass","org.example1.MyFrame");
		doTest("db2ui.xml","testdata-db2ui2.graphml",expected,true);
		doTest("db2ui.xml","testdata-db2ui2.graphml",1);
	}
	
}
