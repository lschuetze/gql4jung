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

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.Vertex;
import edu.uci.ics.jung.graph.DirectedGraph;
/**
 * Counts all variants in this package.
 * Also measures the time for the computation of the query (the time needed
 * to import / build the graph from byte code is not included).
 * Should run with -Xmx64m option !
 * @author jens dietrich
 */
public class CountAzureusVariants extends Utils {
	
	public static void main(String[] args) throws Exception {
		Motif m = loadQuery("queries/cd.xml");
		DirectedGraph<Vertex, Edge> g = loadGraph("data/Azureus3.0.3.4.jar.graphml");
		System.out.println("file read, nodes: " + g.getVertexCount());
		System.out.println("file read, edges: " + g.getEdgeCount());
		countAll(g,m,false);
	}
}
