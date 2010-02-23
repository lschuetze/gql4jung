/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.io.GraphIOException;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;

/**
 * Simple utility to write graphml files. 
 * memory.
 * @author jens dietrich
 */
public abstract class GraphMLWriter<V extends Vertex<E>,E extends Edge<V>> {

	private Writer writer = null;

	public GraphMLWriter(Writer writer) {
		super();
		this.writer = writer;
	}
	public synchronized void writeGraph(DirectedGraph<V, E> g) throws GraphIOException {
		PrintWriter out = new PrintWriter(writer);
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml\">");
		out.println("<graph edgedefault=\"directed\" file=\"\">");
		for (V v:g.getVertices()) {
			write(out,v);
		}
		for (E e:g.getEdges()) {
			write(out,e);
		}
		out.println("</graph>");
		out.println("</graphml>");
	}

	protected void write(PrintWriter out, V v) throws GraphIOException {
		out.print("<node ");
		printAttr(out,"id",v.getId());
		writeAttributes(out,v);
		out.println(" />");
	}
	
	protected void printAttr(PrintWriter out, String name, String value) {
		out.print(name);
		out.print("=\"");
		out.print(value);
		out.print("\" ");
	}
	protected void write(PrintWriter out, E e) throws GraphIOException {
		out.print("<edge ");
		printAttr(out,"id",e.getId());
		printAttr(out,"source",e.getStart().getId());
		printAttr(out,"target",e.getEnd().getId());
		writeAttributes(out,e);
		out.println(" />");		
	}
	
	public synchronized void close() throws IOException {
		if (this.writer!=null) {
			writer.close();
		}
	}
	
	protected abstract void writeAttributes(PrintWriter out,E edge) throws GraphIOException;
	protected abstract void writeAttributes(PrintWriter out,V vertex) throws GraphIOException;

}
