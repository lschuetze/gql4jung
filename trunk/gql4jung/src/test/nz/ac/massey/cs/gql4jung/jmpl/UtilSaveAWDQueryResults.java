package test.nz.ac.massey.cs.gql4jung.jmpl;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.jmpl.GQLImpl;
import nz.ac.massey.cs.gql4jung.util.QueryResults;
import nz.ac.massey.cs.gql4jung.util.QueryResultsExporter2CSV;
import edu.uci.ics.jung.graph.Graph;

public class UtilSaveAWDQueryResults {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Graph g = Tests.loadGraph("ant.jar.graphml");
		Motif m = Tests.loadQuery("awd.xml");
		QueryResults coll = new QueryResults();
		GQL engine = new GQLImpl();
		long t1 = System.currentTimeMillis();
		engine.query(g,m,coll);
		long t2 = System.currentTimeMillis();
		QueryResultsExporter2CSV exporter = new QueryResultsExporter2CSV();
		File file = new File("awd-results.txt");
		exporter.export(coll,file);
		System.out.println("query results exported to " + file.getAbsolutePath());

	}

}
