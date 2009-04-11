package test.nz.ac.massey.cs.gpl4jung.gql;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;

import nz.ac.massey.cs.gpl4jung.DefaultMotif;
import nz.ac.massey.cs.gpl4jung.GQL;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.impl.GQLImpl;
import nz.ac.massey.cs.gpl4jung.xml.XMLMotifReader;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.UserData;

/**Analyzes all queries on projects. 
 * @author Ali
 *
 */
public class QueryAnalyzer {

	/**
	 * @param a
	 * @throws Exception 
	 */
	public static void main(String[] a) throws Exception {
		String INPUT = "Data/";
		File inputFolder = new File(INPUT);
		File[] files = inputFolder.listFiles();
		System.out.println(""+files.length+" projects will be analyzed");
		int counter = 0;
		long totalStartTime = System.currentTimeMillis();
		for(File in:files){
			long startTime = System.currentTimeMillis();
			counter = counter + 1;
			System.out.print("Analyzing ");
			System.out.print(counter);
			System.out.print('/');
			System.out.print(files.length);
			System.out.print(": ");
			System.out.print(in.getName());
			
			String prjFolder = in.getName();
			File prjSubFolder = new File(INPUT+prjFolder);
			File[] prjFiles = prjSubFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.getAbsolutePath().endsWith(".graphml");
				}});
			for(File inp:prjFiles){
				Graph g = readJungGraphFromGraphML(inp.getPath());
				String path = inp.getPath().substring(0,inp.getPath().lastIndexOf("\\")+1);
				g.addUserDatum("path", path, UserData.SHARED);
				g.addUserDatum("query", "abstraction_coupling", UserData.SHARED);
				Motif q = (DefaultMotif) readMotif("xml/abstraction_coupling.xml");
				GQL gql = new GQLImpl();
				ResultCollector listener = new ResultCollector();
				gql.query(g, q, listener);
				System.out.println("\nQuery1 executed");
				Motif q2 = (DefaultMotif) readMotif("xml/circular_dependency.xml");
				g.setUserDatum("query", "circular_dependency", UserData.SHARED);
				GQL gql2 = new GQLImpl();
				ResultCollector listener2 = new ResultCollector();
				gql2.query(g, q2, listener2);
				System.out.println("Query2 executed");
				Motif q3 = (DefaultMotif) readMotif("xml/db2ui_dependency.xml");
				g.setUserDatum("query", "db2ui_dependency", UserData.SHARED);
				GQL gql3 = new GQLImpl();
				ResultCollector listener3 = new ResultCollector();
				gql3.query(g, q3, listener3);
				System.out.println("Query3 executed");
				Motif q4 = (DefaultMotif) readMotif("xml/multiple_clusters.xml");
				g.setUserDatum("query", "multiple_clusters", UserData.SHARED);
				GQL gql4 = new GQLImpl();
				ResultCollector listener4 = new ResultCollector();
				gql4.query(g, q4, listener4);
				System.out.println("Query4 executed");
			}
			long endTime = System.currentTimeMillis();
	        System.out.println("Total elapsed time in analyzing "+in.getName()+ " project (ms): "+ (endTime-startTime));
			System.out.println("Project analyzed successfully.");
		}
		long totalEndTime = System.currentTimeMillis();
        System.out.println("Total elapsed time in analyzing all projects (ms): "+ (totalEndTime-totalStartTime));

	}
	private static Graph readJungGraphFromGraphML(String graphSource) throws Exception {	
		GraphMLFile input = new GraphMLFile();
		Reader reader = new FileReader(graphSource);
		Graph g = new DirectedSparseGraph();
		g =	input.load(reader);
		reader.close(); 
		return g;
	}
	private static Motif readMotif(String motifSource) throws Exception {
		XMLMotifReader r = new XMLMotifReader();
		DefaultMotif q = (DefaultMotif) r.read(new FileInputStream (motifSource));
		return q;
		
	}
}
