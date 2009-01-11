package test.nz.ac.massey.cs.gpl4jung.gql;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.gpl4jung.DefaultMotif;
import nz.ac.massey.cs.gpl4jung.GQL;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.MotifInstance;
import nz.ac.massey.cs.gpl4jung.xml.XMLMotifReader;
import nz.ac.massey.cs.utils.odem2graphml.Odem2GraphML;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLFile;

public class GQLTests {
	static {
		// only necessary when running inside massey
		System.getProperties().put( "proxySet", "true" );
		System.getProperties().put( "proxyHost", "tur-cache" );
		System.getProperties().put( "proxyPort", "8080" );
	}
	
	
	private GQL gql = null; // TODO
	@Before
	public void init() {
		// FIXME must initialize GQL here
		
	}
	@After
	public void release() {
		this.gql=null;
		
	}
	// see also http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/io/GraphMLFile.html
	private Graph readJungGraphFromGraphML(String graphSource) throws Exception {
		GraphMLFile input = new GraphMLFile();
		Reader reader = new FileReader(graphSource);
		Graph g = input.load(reader);
		
		reader.close();
		return g;
	}
	private Graph readJungGraphFromGraphML(Reader reader) throws Exception {
		GraphMLFile input = new GraphMLFile();
		Graph g = input.load(reader);
		reader.close();
		return g;
	}
	private Graph readJungGraphFromODEM(String odemSource) throws Exception {
		Odem2GraphML converter = new Odem2GraphML();
		File odem = new File(odemSource);
		Reader odemReader = new FileReader(odem);
		// jens: we try to do this in memory, if this does not scale,
		// change to filewriter using tmp files
		StringWriter writer = new StringWriter();
		converter.convert(odemReader, writer);
		odemReader.close();
		writer.close();
		String graphML = writer.getBuffer().toString();
		StringReader graphmlReader = new StringReader(graphML);
		return readJungGraphFromGraphML(graphmlReader);
	}
	private Motif readMotif(String motifSource) throws Exception {
		// TODO refer to your own MotifParser here (unmarshal)
		// use XMLMotifReader

		return null;
		
	}
	@Test
	//No decoupling through abstraction
	public void test1 () throws Exception {
		Graph g = this.readJungGraphFromGraphML("test_examples/abstraction.graphml");
		XMLMotifReader r = new XMLMotifReader();
		DefaultMotif q = (DefaultMotif) r.read(new FileInputStream ("xml/query1.xml"));
		ResultCollector rc = new ResultCollector();
		this.gql.query(g,q,rc);
		List<MotifInstance> results = rc.getInstances();
		
		// when client "MyApplication" accesses both "Animal" abstract class & "Horse" Impl of animal
		assertEquals(1,results.size());
		MotifInstance instance1 = results.get(0);
		assertEquals("Animal",instance1.getVertex("service").getUserDatum("name"));
		assertEquals("MyApplication",instance1.getVertex("client").getUserDatum("name"));
		assertEquals("Horse",instance1.getVertex("service_impl").getUserDatum("name"));
		//assertEquals(expected, actual)
	}
	@Test
	//to test circular dependency between classes and packages. 
	public void test2 () throws Exception {
		Graph g = this.readJungGraphFromGraphML("test_examples/dependency.graphml");
		XMLMotifReader r = new XMLMotifReader();
		DefaultMotif q = (DefaultMotif) r.read(new FileInputStream ("xml/query2.xml"));
		ResultCollector rc = new ResultCollector();
		this.gql.query(g,q,rc);
		List<MotifInstance> results = rc.getInstances();
		
		assertEquals(1,results.size());
		MotifInstance instance1 = results.get(0);
		assertEquals("Class1",instance1.getVertex("Class1").getUserDatum("name"));
		assertEquals("Class3",instance1.getVertex("Class3").getUserDatum("name"));
		assertEquals("Class2",instance1.getVertex("Class2").getUserDatum("name"));
		//assertEquals(expected, actual)
	}
	
	@Test
	// to test: no decoupling through abstraction
	// class depends on ui layer and db layer
	public void test3() throws Exception {
		Graph g = this.readJungGraphFromGraphML("test_examples/separation.graphml");
		XMLMotifReader r = new XMLMotifReader();
		DefaultMotif q = (DefaultMotif) r.read(new FileInputStream ("xml/query3.xml"));
		ResultCollector rc = new ResultCollector();
		this.gql.query(g,q,rc);
		List<MotifInstance> results = rc.getInstances();
		
		assertEquals(1,results.size());
		MotifInstance instance1 = results.get(0);
		assertEquals("MyClass",instance1.getVertex("ui").getUserDatum("name"));
		assertEquals("MyClass",instance1.getVertex("db").getUserDatum("name"));
		//assertEquals(expected, actual)
	}
	
	
		
}

