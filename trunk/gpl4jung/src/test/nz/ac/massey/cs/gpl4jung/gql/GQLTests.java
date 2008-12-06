package test.nz.ac.massey.cs.gpl4jung.gql;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import nz.ac.massey.cs.gpl4jung.GQL;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.MotifInstance;
import nz.ac.massey.cs.gpl4jung.MotifReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLFile;

public class GQLTests {
	
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
	private Graph readJungGraph(String graphSource) throws Exception {
		GraphMLFile input = new GraphMLFile();
		Reader reader = new FileReader(graphSource);
		Graph g = input.load(reader);
		reader.close();
		return g;
	}
	private Motif readMotif(String motifSource) throws Exception {
		GraphMLFile input = new GraphMLFile();
		Reader motif_reader = new FileReader(motifSource);
		Motif q = (Motif) input.load(motif_reader);
		motif_reader.close();
		return q;
		
	}
	@Test
	public void test1 () throws Exception {
		Graph g = this.readJungGraph("xml/testgraphs/graph1.xml");
		Motif q = this.readMotif("xml/testgraphs/query1.xml");
		ResultCollector rc = new ResultCollector();
		this.gql.query(g,q,rc);
		List<MotifInstance> results = rc.getInstances();
		
		// TODO asserts
		
	}
}
