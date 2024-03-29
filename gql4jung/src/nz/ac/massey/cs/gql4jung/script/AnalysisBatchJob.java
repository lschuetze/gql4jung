/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.script;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifReaderException;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.GraphMLReader;
import nz.ac.massey.cs.gql4jung.io.JarReader;
import nz.ac.massey.cs.gql4jung.jmpl.GQLImpl;
import nz.ac.massey.cs.gql4jung.util.QueryResults;
import nz.ac.massey.cs.gql4jung.util.QueryResults.QueryResultListener;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;

/**
 * Utility to batch process input files.
 * @author jens dietrich
 */
public class AnalysisBatchJob {
	public static final String ROOT = "./";
	public static final String INPUT_DATA_FOLDER = "input";
	public static final String QUERY_FOLDER = ROOT+"queries";
	public static final String OUTPUT_FOLDER = ROOT+"output";
	public static final String SUMMARY = ROOT+"summary.csv";
	public static final String LOG_FILE = ROOT+"analysis.log";
	public static final String SEP = ",";
	public static final Logger LOGGER = Logger.getLogger("gql4jung-batch-job");
	public static final String NL = System.getProperty("line.separator");

	static {
		try {
			org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// prepare
		File data = new File(INPUT_DATA_FOLDER);		
		assert data.isDirectory() && data.exists();
		File queries = new File(QUERY_FOLDER);
		assert queries.isDirectory() && queries.exists();
		File output = new File(OUTPUT_FOLDER);
		if (!output.exists()) {
			log("Create output folder " + output);
			output.mkdir();
		}
		// delete old summary file
		//File summary = new File(SUMMARY);
		//if (summary.exists()) summary.delete();

		
		// queries
		File[] queryFiles = queries.listFiles();
		File[] dataFiles = data.listFiles();
		for (int i=0;i<queryFiles.length;i++) {
			for (int j=0;j<dataFiles.length;j++) {
				if (queryFiles[i].getAbsolutePath().endsWith(".xml")) { // to make sure we dont try .svn folders
					String cursorLog = "graph "+(j+1)+"/"+dataFiles.length;
					log("query ",i+1,"/",queryFiles.length," ",cursorLog);
					try {
						analyse(queryFiles[i],dataFiles[j],cursorLog);
						System.gc();
					}
					catch (Exception x) {
						LOGGER.error("analysis error",x);
					}
				}
			}
		}
	}
	
	
	public static void analyse (File querySource,final File graphSource,final String cursorLog) throws Exception {
		DirectedGraph<Vertex,Edge> graph = loadGraph(graphSource);
		Motif motif = loadMotif(querySource);
		
		if (graph.getVertices()==null) {
			log("Skipping ",graphSource.getName()," - no vertices found");
			return;
		}
		
		QueryResults results = new QueryResults();
		results.addListener(new QueryResultListener() {

			@Override
			public void progressMade(int progress, int total) {
				log(progress,"/",total," done in ",graphSource.getName()," ",cursorLog);
			}

			@Override
			public void resultsChanged(QueryResults source) {
				
			}
			
		});
		log("Starting query ",querySource.getName()," on data ",graphSource.getName());
		
		// query
		GQL engine = new GQLImpl();
		long before = System.currentTimeMillis();
		engine.query(graph, motif, results,true);
		long after = System.currentTimeMillis();
		log("Query finished, this took ",""+(after-before)," ms");
		
		// export results
		//QueryResultsExporter2CSV exporter = new QueryResultsExporter2CSV();
		//File out = getOutputFile(querySource,graphSource);
		//exporter.export(results, out);
		//log("Query results exported to ",out.getAbsolutePath());
		
		String time = DurationFormatUtils.formatDuration(after-before,"H:m:s.S",true);
		printSummary(querySource,graphSource,results,time,graph);
		results.reset();
		
		// graphSource.delete();
	
	} 
	
	private static void printSummary(File querySource, File graphSource,QueryResults results,String time,Graph graph) throws IOException {
		//File summary = new File(SUMMARY);
		FileWriter out = new FileWriter(SUMMARY,true);
		/*
		if (!summary.exists()) {
			StringBuffer b = new StringBuffer()
			.append("graph source")
			.append(SEP)
			.append("query source")
			.append(SEP)
			.append("vertex count")
			.append(SEP)
			.append("edge count")
			.append(SEP)
			.append("instances")
			.append(SEP)
			.append("variants")
			.append(SEP)
			.append("time")
			.append(NL);
		out.write(b.toString());
		}
		*/
		StringBuffer b = new StringBuffer()
			.append(graphSource.getName())
			.append(SEP)
			.append(querySource.getName())
			.append(SEP)
			.append(graph.getVertices().size())
			.append(SEP)
			.append(graph.getEdges().size())
			.append(SEP)
			.append(results.getNumberOfGroups())
			.append(SEP)
			.append(results.getNumberOfInstances())
			.append(SEP)
			.append(time)
			.append(NL);
		out.write(b.toString());
		out.close();
		log("Query result summary added to ",SUMMARY);
	}


	private static void log(Object... s) {
		StringBuffer b = new StringBuffer();
		for (Object t:s) {
			b.append(t);
		}
		LOGGER.info(b.toString());
	}


	private static Motif loadMotif(File file) throws IOException, MotifReaderException {
		InputStream in = new FileInputStream(file);
        return new XMLMotifReader().read(in);
	}


	private static DirectedGraph<Vertex,Edge> loadGraph(File file) throws Exception {
		log("Loading graph from ",file.getAbsolutePath());
		if (file.getAbsolutePath().endsWith(".jar")) {
			JarReader r = new JarReader(file);
			DirectedGraph<Vertex,Edge> g = new JarReader(file).readGraph();
			r.close();
			return g;
		}
		else if (file.getAbsolutePath().endsWith(".graphml")) {
			GraphMLReader r = new GraphMLReader(new FileReader(file));
			DirectedGraph<Vertex,Edge> g = r.readGraph();
			r.close();
			return g;
		}
		else throw new IllegalArgumentException("non existing file or wrong file type: "+file.getAbsolutePath());

	}

	private static File getOutputFile(File querySource,File graphSource) {
		String out = OUTPUT_FOLDER + '/' + querySource.getName() + '/' + graphSource.getName() + ".csv";
		out = removeWhitespaces(out);
		return new File(out);
	}


	private static String removeWhitespaces(String s) {
		StringBuffer b = new StringBuffer();
		for (char c:s.toCharArray()) {
			if (Character.isWhitespace(c)) {
				b.append('_');
			}
			else {
				b.append(c);
			}
		}
		return b.toString();
	}

}
