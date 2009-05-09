package test.nz.ac.massey.cs.gql4jung.gql;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.gql4jung.DefaultMotif;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.MotifInstance2Graph;
import nz.ac.massey.cs.gql4jung.impl.GQLImpl;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphMLFile;


/**Analyzes all queries on projects. 
 * @author Ali
 */
public class QueryAnalyzer {

	public static void main(String[] a) throws Exception {
		String INPUT = "Data/"; //the main folder containing subfolders for every project
		File inputFolder = new File(INPUT);
		File[] projects = inputFolder.listFiles();
		System.out.println(""+projects.length+" projects will be analyzed\n");
		int counter = 0;
		int instanceCounter =1;
		long totalStartTime = System.currentTimeMillis();
		//looping over all projects in main folder
		for(File in:projects){
			long startTime = System.currentTimeMillis();
			counter = counter + 1;
			System.out.print("Analyzing ");
			System.out.print(counter);
			System.out.print('/');
			System.out.print(projects.length);
			System.out.print(": ");
			System.out.print(in.getName()+"\n");
			//individual project folder in main folder
			String prjctFolder = in.getName();
			File projectFolder = new File(INPUT+prjctFolder);
			//getting project files with .graphml extension
			File[] prjFiles = projectFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.getAbsolutePath().endsWith(".graphml");
				}});
			for(File inp:prjFiles){
				Graph g = readJungGraphFromGraphML(inp.getPath());
				String path = inp.getPath().substring(0,inp.getPath().lastIndexOf("\\")+1);
				String queryInput = "xml/"; //queries main folder
				File queryInputFolder = new File(queryInput);
				//getting query files from xml folder
				File[] queryFiles = queryInputFolder.listFiles(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.getAbsolutePath().endsWith(".xml");
					}});
				for(File query:queryFiles){
					Motif q = (DefaultMotif) readMotif(query.getPath());
					GQL gql = new GQLImpl();
					ResultCollector listener = new ResultCollector();
					gql.query(g, q, listener);
					List<MotifInstance> motifInstance = listener.getInstances();
					System.out.print("\tQuery " + query.getName() + " executed. Writing "
							+ motifInstance.size() + " instances found. ");
					if(motifInstance!=null){
						String newPath = path + query.getName().substring(0,query.getName().lastIndexOf("."));
						for(Iterator iter=motifInstance.iterator();iter.hasNext();){
							MotifInstance2Graph motifInstance2Graphml = new MotifInstance2Graph();
							String instanceNum = newPath + "\\result"+instanceCounter+".graphml";
							MotifInstance instance = (MotifInstance)iter.next();
							motifInstance2Graphml.asGraph(instance);
							instanceCounter++;
							instanceNum=null;
						}
						newPath=null;
					}
					System.out.print("Done\n");
					instanceCounter = 1; //reset counter
				}
			}
			long endTime = System.currentTimeMillis();
	        System.out.println("Total elapsed time in analyzing "+in.getName()+ " project (ms): "+ (endTime-startTime));
			System.out.println("Project analyzed successfully.\n");
			System.gc();
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
