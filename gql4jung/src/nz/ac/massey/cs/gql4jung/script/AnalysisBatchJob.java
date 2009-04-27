package nz.ac.massey.cs.gql4jung.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphMLFile;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifReaderException;
import nz.ac.massey.cs.gql4jung.impl.GQLImpl;
import nz.ac.massey.cs.gql4jung.util.QueryResults;
import nz.ac.massey.cs.gql4jung.util.QueryResultsExporter2CSV;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;

public class AnalysisBatchJob {
	public static final String ROOT = "batch/";
	public static final String INPUT_DATA_FOLDER = ROOT+"inputdata";
	public static final String QUERY_FOLDER = ROOT+"queries";
	public static final String OUTPUT_FOLDER = ROOT+"output";
	public static final String SUMMARY = ROOT+"summary.csv";
	public static final String LOG_FILE = ROOT+"analysis.log";
	public static final String SEP = ",";
	public static final Logger LOGGER = Logger.getLogger("gql4jung-batch-job");
	public static final String NL = System.getProperty("line.separator");

	static {
		try {
			// delete old log file
			File log = new File(LOG_FILE);
			if (log.exists()) log.delete();	
			
			// setup logger
			Layout layout = new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN);
			Appender app1 = new FileAppender(layout,LOG_FILE); 
			Appender app2 = new ConsoleAppender(layout,ConsoleAppender.SYSTEM_OUT);
			LOGGER.addAppender(app1);
			LOGGER.addAppender(app2);
			LOGGER.setLevel(Level.ALL);
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
		File summary = new File(SUMMARY);
		if (summary.exists()) summary.delete();

		
		// queries
		File[] queryFiles = queries.listFiles();
		File[] dataFiles = data.listFiles();
		for (int i=0;i<queryFiles.length;i++) {
			for (int j=0;j<dataFiles.length;j++) {
				log("query ",i+1,"/",queryFiles.length," graph ",j+1,"/",dataFiles.length);
				try {
					analyse(queryFiles[i],dataFiles[j]);
				}
				catch (Exception x) {
					LOGGER.error("analysis error",x);
				}
			}
		}
	}
	
	
	public static void analyse (File querySource,File graphSource) throws IOException, MotifReaderException {
		Graph graph = loadGraph(graphSource);
		Motif motif = loadMotif(querySource);
		
		QueryResults results = new QueryResults();
		log("Starting query ",querySource.getName()," on data ",graphSource.getName());
		
		// query
		GQL engine = new GQLImpl();
		long before = System.currentTimeMillis();
		engine.query(graph, motif, results);
		long after = System.currentTimeMillis();
		log("Query finished, this took ",""+(after-before)," ms");
		
		// export results
		QueryResultsExporter2CSV exporter = new QueryResultsExporter2CSV();
		File out = getOutputFile(querySource,graphSource);
		exporter.export(results, out);
		log("Query results exported to ",out.getAbsolutePath());
		
		String time = DurationFormatUtils.formatDuration(after-before,"H:m:s.S",true);
		printSummary(querySource,graphSource,results,time,graph);
		results.reset();
	
	} 
	
	private static void printSummary(File querySource, File graphSource,QueryResults results,String time,Graph graph) throws IOException {
		File summary = new File(SUMMARY);
		FileWriter out = new FileWriter(summary,true);
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
		log("Query result summary added to ",summary.getAbsolutePath());
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


	private static Graph loadGraph(File file) throws IOException {
		GraphMLFile input = new GraphMLFile();
        Reader reader = new FileReader(file);
        Graph g = new DirectedSparseGraph();
        log("Loading graphml file ",file.getAbsolutePath());
        g =	input.load(reader);
        reader.close();
        return g;
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
