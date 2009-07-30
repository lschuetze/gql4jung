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

import java.io.FileWriter;

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.GraphMLWriter;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Script to create a GraphML file for Azureus. The main reason to analyse the 
 * graphml input instead of using the jar directly is that the bytecode parser
 * (DepFinder) needs more memory than the graph query engine. We are interested
 * to find out how much memory the query engine needs.
 * @author jens dietrich
 */
public class CreateAzureusGraphML extends Utils {
	
	public static void main(String[] args) throws Exception {
		String in = "data/Azureus3.0.3.4.jar";
		String out = "data/Azureus3.0.3.4.jar.graphml";
		DirectedGraph<Vertex, Edge> g = loadGraph("data/Azureus3.0.3.4.jar");
		System.out.println("file read, nodes: " + g.getVertexCount());
		System.out.println("file read, edges: " + g.getEdgeCount());
		FileWriter writer = new FileWriter(out);
		GraphMLWriter converter = new GraphMLWriter(writer);
		converter.writeGraph(g);
		System.out.println("graph exported to " + out);
		converter.close();
		
	}
}
