/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.benchmarking.crocopat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import edu.uci.ics.jung.graph.DirectedGraph;
import nz.ac.massey.cs.gql4jung.*;
import nz.ac.massey.cs.gql4jung.io.JarReader;

/**
 * Utility to generate a RSF file from a jung/gql4jung graph.
 * RSF is the input format for Crocopat.
 * @author jens dietrich
 */

public class Jung2RSF {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		File in = new File("crocopat/Azureus3.0.3.4.jar");
		JarReader reader = new JarReader(in);
		DirectedGraph<Vertex,Edge> graph = reader.readGraph();
		File target = new File(in.getAbsolutePath()+".rsf");
		PrintStream out = new PrintStream(new FileOutputStream(target));
		convert(graph,out);
		out.close();
		System.out.println("convertion succeeded, output written to " + target.getAbsolutePath());
	}

	public static void convert (DirectedGraph<Vertex,Edge> graph,PrintStream out) throws IOException {
		// relationships
		for (Edge e:graph.getEdges()) {
			out.print(e.getType());
			out.print(" ");
			out.print(e.getStart().getFullname());
			out.print(" ");
			out.println(e.getEnd().getFullname());
		}
		// name spaces
		for (Vertex v:graph.getVertices()) {
			out.print("namespace");
			out.print(" ");
			out.print(v.getFullname());
			out.print(" ");
			out.println(v.getNamespace());
			
		}
	}
}
