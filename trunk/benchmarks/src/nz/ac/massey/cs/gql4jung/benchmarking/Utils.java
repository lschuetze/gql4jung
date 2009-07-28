/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.benchmarking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.ResultListener;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.GraphMLReader;
import nz.ac.massey.cs.gql4jung.io.JarReader;
import nz.ac.massey.cs.gql4jung.jmpl.GQLImpl;
import nz.ac.massey.cs.gql4jung.util.ResultCollector;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Utility methods.
 * @author jens dietrich
 */
public class Utils {

	public static void countAll(DirectedGraph<Vertex,Edge> g, Motif m, boolean aggregationOn) {
		System.out.println("Starting query");
		GQLImpl engine = new GQLImpl();
	
		ResultCollector rs = new ResultCollector();
		long before = System.currentTimeMillis();
		engine.query(g, m, rs, aggregationOn);
		long after = System.currentTimeMillis();
		
		System.out.println(""+rs.getInstances().size()+" instances found");
		System.out.println("time taken "+(after-before)+" ms");
	}

	public static void findFirst(DirectedGraph<Vertex,Edge> g, Motif m) {
		System.out.println("Starting query");
		GQLImpl engine = new GQLImpl();
	
		ResultListener rs = new ResultListener() {

			@Override
			public void done() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean found(MotifInstance instance) {
				System.out.println("Result found");
				return false;
			}

			@Override
			public void progressMade(int progress, int total) {
			}
			
		};
		long before = System.currentTimeMillis();
		engine.query(g, m, rs, false);
		long after = System.currentTimeMillis();
		
		System.out.println("First instance found, this took: "+(after-before)+ " ms");
	}
	
	static DirectedGraph<Vertex, Edge> loadGraph(String src) throws Exception {
		if (src.endsWith(".jar")) return new JarReader(new File(src)).readGraph();
		else if (src.endsWith(".graphml")) return new GraphMLReader(new FileReader(src)).readGraph();
		else throw new IllegalArgumentException("non existing file or wrong file type: "+src);
	}

	static Motif loadQuery(String src) throws Exception {
	    return new XMLMotifReader().read(new FileInputStream(src));
	}

}
