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
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphReader;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;

/**
 * Simple utility to read graphml files. Not very effective, stores graph and xml at the same time in
 * memory.
 * @author jens dietrich
 */
public abstract class GraphMLReader<V extends Vertex<E>,E extends Edge<V>> implements GraphReader<DirectedGraph<V,E>,V,E> {

	private Reader reader = null;
	private int idCounter = 0;

	public GraphMLReader(Reader reader) {
		super();
		this.reader = reader;
	}
	@Override
	public synchronized DirectedGraph<V,E> readGraph() throws GraphIOException {
		
		Map<String,V> vertices = new HashMap<String,V>();	
		Map<String,E> edges = new HashMap<String,E>();
		// parse
		SAXBuilder builder = new SAXBuilder();
		try {
			Namespace NS_GRAPHML = Namespace.getNamespace("http://graphml.graphdrawing.org/xmlns/graphml");
			Document doc = builder.build(reader);
			Element root = doc.getRootElement();
			assert "graphml".equals(root.getName());
			Element eGraph = root.getChild("graph",NS_GRAPHML); 
			for (Object o:eGraph.getChildren("node",NS_GRAPHML)) {
				if (o instanceof Element) {
					buildVertex(vertices,(Element)o);
				}
			}
			for (Object o:eGraph.getChildren("edge",NS_GRAPHML)) {
				if (o instanceof Element) {
					E e = buildEdge(vertices,(Element)o);
					if (edges.containsKey(e.getId())) {
						throw new GraphIOException("There are two edges with the same id " + e.getId() + " in the graph");
					}
					edges.put(e.getId(),e);
				}
			}
			// TODO: at this stage both the xml doc and the graph are in memory
			// we could gc the doc before we continue
			// build graph
			
		} catch (Exception e) {
			throw new GraphIOException(e);
		}
		DirectedGraph<V, E> graph = new DirectedSparseGraph<V, E> ();
		for (V v:vertices.values()) {
			graph.addVertex(v);
		}
		for (E e:edges.values()) {
			graph.addEdge(e,e.getStart(),e.getEnd());
		}
		return graph;
		
	}
	protected String createNextId(String prefix) {
		this.idCounter = idCounter+1;
		return prefix+idCounter;
	}
	protected E buildEdge(Map<String, V> vertices, Element e) throws GraphIOException {
		E edge = createNewEdge();
		
		String id = e.getAttributeValue("id");
		if (id==null) id = createNextId("e");
		edge.setId(id);
		
		String source = e.getAttributeValue("source");
		if (source==null) throw new GraphIOException("Source attribute missing in edge " + id);
		V v1 = vertices.get(source);
		if (v1 == null) throw new GraphIOException("No vertex found for id " + source);
		edge.setStart(v1);
		
		String target = e.getAttributeValue("target");
		if (target==null) throw new GraphIOException("Target attribute missing in edge " + id);
		V v2 = vertices.get(target);
		if (v2 == null) throw new GraphIOException("No vertex found for id " + source);
		edge.setEnd(v2);
		
		readAttributes(edge,e);
				
		return edge;
	}
	
	private V buildVertex(Map<String, V> vertices, Element e) throws GraphIOException {
		V v = createNewVertex();
		String id = e.getAttributeValue("id");
		if (id==null) id = this.createNextId("v");
		
		readAttributes(v,e);

		// register
		if (vertices.containsKey(v.getId())) {
			throw new GraphIOException("There are two nodes with the same id " + v.getId() + " in the graph");
		}
		vertices.put(v.getId(),v);
		return v;
	}
	@Override
	public synchronized void close() throws GraphIOException {
		if (this.reader!=null) {
			try {
				this.reader.close();
			} catch (IOException e) {
				throw new GraphIOException(e);
			}
		}
	}
	
	protected abstract E createNewEdge();
	protected abstract V createNewVertex();
	protected abstract void readAttributes(E edge,Element e) throws GraphIOException;
	protected abstract void readAttributes(V v,Element e) throws GraphIOException;

}
