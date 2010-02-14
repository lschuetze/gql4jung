/**
 * Copyright 2010 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import edu.uci.ics.jung.graph.DirectedGraph;
import nz.ac.massey.cs.codeanalysis.JarReader;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifReaderException;
import nz.ac.massey.cs.gql4jung.ResultListener;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.GraphMLReader;
import nz.ac.massey.cs.gql4jung.jmpl.GQLImpl;
import nz.ac.massey.cs.gql4jung.jmpl.MultiThreadedGQLImpl;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;

/**
 * Simple executable class to run queries from a command line interface.
 * @author jens dietrich
 */
public class Run {

	// command line parameters (key-value)
	// the GQL engine class name, optional parameter
	public static final String GQLCLASS = "-engine";
	// the listener class name, optional parameter
	public static final String LISTENER = "-listener";
	// the number of threads to be used, optional parameter
	public static final String THREADS = "-threads";
	// the location of the input file (jar or graphml) - required
	public static final String INPUT = "-input";
	// the location of the query - required
	public static final String MOTIF = "-motif";
	// flags (key only)
	public static final String[] FLAGS = {"-variants"};
	// whether to calculate all variants, optional parameter
	public static final String COMPUTE_VARIANTS = FLAGS[0];
	
	// default values
	public static final String DGQLCLASS = nz.ac.massey.cs.gql4jung.jmpl.GQLImpl.class.getName();
	
	public static void main(String[] args) throws Exception {
		
		//Properties logProps = new Properties();
		//logProps.load(new FileReader("./log4j.properties"));
		//org.apache.log4j.PropertyConfigurator.configure(logProps);
		
		org.apache.log4j.LogManager.getRootLogger().setLevel(Level.WARN);
		
		
		Map<String,Object> param = parseArgs(args);
		
		GQL engine = (GQL)param.get(GQLCLASS);
		Integer i = (Integer) param.get(THREADS);
		if (i==null || i<2) {
			if (engine==null) {
				engine = new GQLImpl();	
			}
		}
		else if (engine instanceof MultiThreadedGQLImpl) {
			((MultiThreadedGQLImpl)engine).setNumberOfThreads(i);
		}
		else {
			engine = new MultiThreadedGQLImpl(i);
		}
		log("Using engine: ",engine);
		
		File graphSource = (File) param.get(INPUT);
		if (graphSource==null) {
			throw new IllegalArgumentException("No graph source defined, use " + INPUT + " parameter to define source");
		}
		DirectedGraph<Vertex,Edge> graph = loadGraph(graphSource);
		if (graph.getVertices()==null) {
			throw new IllegalArgumentException("The graph imported from "+graphSource.getName()+" does not contain nodes");
		}
		log("Using graph: ",graphSource.getAbsolutePath());
		
		File motifSource = (File) param.get(MOTIF);
		Motif motif = loadMotif(motifSource);
		log("Using motif: ",motifSource.getAbsolutePath());
		
		ResultListener listener = (ResultListener) param.get(LISTENER);
		if (listener==null) {
			listener = new ResultCounter();
		}
		log("Using result listener: ",listener);
		
		Boolean computeVariants = (Boolean) param.get(COMPUTE_VARIANTS);
		if (computeVariants==null) computeVariants = false;
		log("Compute variants: ",computeVariants);
		
		engine.query(graph, motif, listener, !computeVariants);

	}

	private static Map<String, Object> parseArgs(String[] args) throws Exception{
		Map<String,Object> param = new HashMap<String,Object>();	
		int pos = 0;
		while (pos<args.length) {
			String arg = args[pos];
			if (isFlag(arg)) {
				param.put(arg,true);
				pos=pos+1;
			}
			else {
				if (pos==args.length-1) {
					throw new IllegalArgumentException("Parameter " + arg + " must be followed by a value");
				}
				String value = args[pos+1];
				addEntry(arg,value,param);
				pos=pos+2;
			}
		}
		return param;
	}

	private static void addEntry(String arg, String value,Map<String, Object> param) throws Exception {
		if (GQLCLASS.equals(arg)) {
			GQL engine = (GQL)parseObject(GQL.class,arg);	
			param.put(arg, engine);
		}
		else if (LISTENER.equals(arg)) {
			ResultListener listener = (ResultListener)parseObject(ResultListener.class,value);	
			param.put(arg, listener);
		}
		else if (THREADS.equals(arg)) {
			int v = Integer.parseInt(value);
			param.put(arg, v);
		}
		else if (INPUT.equals(arg)) {
			File f = parseFile(value,true,true);
			param.put(arg, f);
		}
		else if (MOTIF.equals(arg)) {
			File f = parseFile(value,true,true);
			param.put(arg, f);
		}

		else {
			throw new IllegalArgumentException("Unknow parameter " + arg + " -> " + value);
		}
		
	}

	private static File parseFile(String arg, boolean mustExist, boolean mustBeFile) {
		File f = new File(arg);
		if (mustExist && !f.exists()) {
			throw new IllegalArgumentException("File " + f.getAbsolutePath() + " does not exist");
		}
		if (mustBeFile && f.isDirectory()) {
			throw new IllegalArgumentException("File " + f.getAbsolutePath() + " must be a file, not a folder");
		}
		return f;
	}

	private static Object parseObject(Class clazz,String arg) throws Exception {
		Object instance = Class.forName(arg).newInstance();
		if (!clazz.isAssignableFrom(instance.getClass())) {
			throw new IllegalArgumentException("Class " + instance.getClass() + " is not a subtype of " + clazz);
		}
		return instance;
	}

	private static boolean isFlag(String arg) {
		for (String flag:FLAGS) {
			if (flag.equals(arg)) return true;
		}
		return false;
	}

	static void log(Object...parts) {
		for (Object s:parts) {
			System.out.print(s);
		}
		System.out.println();
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
	
}
