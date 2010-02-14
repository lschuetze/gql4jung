package nz.ac.massey.cs.gql4jung.script;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nz.ac.massey.cs.codeanalysis.JarReader;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.GraphMLWriter;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.io.GraphIOException;

public class Jars2GraphML {
	
	private static boolean ignoreExisting = true;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if (args.length!=2) throw new IllegalArgumentException("Arguments must be \"inputFileOrFolder outputFileOrFolder\"");
		File input = new File(args[0]);
		File output = new File(args[1]);
		
		if (!input.exists()) throw new IllegalArgumentException("Input source " + input + " does not exist");
		if (output.exists() && !output.isDirectory()) throw new IllegalArgumentException("output " + output + " is not a folder");
		
		List<File> inputFiles = new ArrayList<File>();
		if (input.isFile() && isJar(input)) {
			inputFiles.add(input);
		}
		else if (input.isDirectory()) {
			for (File f:input.listFiles()) {
				if (isJar(f) || f.isDirectory()) {
					inputFiles.add(f);
				}
			}
		}
		else {
			throw new IllegalArgumentException("Can only accept archives or folders as input");
		}
		// prepare output
		if (!output.exists()) {
			output.mkdir();
		}
		// start processing
		int size = inputFiles.size();
		for (int i=0;i<size;i++) {
			File nextIn = inputFiles.get(i);
			String progress = nextIn.getAbsolutePath() + "("+(i+1)+"/"+size+")";
			File nextOut = new File(output.getAbsolutePath() + File.separator + nextIn.getName() + ".graphml");
			if (ignoreExisting && nextOut.exists()) {
				log("Using existing conversion for " + progress);
			}
			else {
				log("Converting " + progress);
				List<File> jars = new ArrayList<File>();
				if (isJar(nextIn)) {
					jars.add(nextIn);
				}
				else if (nextIn.isDirectory()){
					for (File fs:nextIn.listFiles()) {
						if (isJar(fs)) {
							jars.add(fs);
						}
					}
					if (jars.isEmpty()) {
						throw new IllegalArgumentException("No jars found inside folder " + nextIn.getAbsolutePath());
					}
					
				}
				JarReader r = new JarReader(jars);
				DirectedGraph<Vertex,Edge> g = r.readGraph();
				r.close();
				log("Graph imported");
				
				Writer out = new BufferedWriter(new FileWriter(nextOut));
				GraphMLWriter w = new GraphMLWriter(out);
				w.writeGraph(g);
				w.close();
				log("Graph exported to " + nextOut);
			}
		}
		
		
	}
	private static void log(String msg) {
		System.out.println(msg);
	} 
	private static boolean isJar(File f) {
		if  (f!=null && f.exists()) {
			String n = f.getAbsolutePath();
			return n.endsWith(".jar");
		}
		return false;
	}
}
