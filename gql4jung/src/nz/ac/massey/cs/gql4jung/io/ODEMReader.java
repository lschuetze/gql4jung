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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphReader;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;

/**
 * Reads ODEM files.
 * @author jens dietrich
 */
public class ODEMReader implements GraphReader<DirectedGraph<Vertex,Edge>, Vertex, Edge> {

	private Reader reader = null;

	public ODEMReader(Reader reader) {
		super();
		this.reader = reader;
	}
	@Override
	public synchronized DirectedGraph<Vertex, Edge> readGraph() throws GraphIOException {
		DirectedGraph<Vertex, Edge> graph = new DirectedSparseGraph<Vertex, Edge> ();
		Map<String,Vertex> vertices = new HashMap<String,Vertex>();	
		Collection<Edge> edges = new ArrayList<Edge>();
		// parse
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(reader);
			Element root = doc.getRootElement();
			assert "ODEM".equals(root.getName());
			List<Element> eContexts = root.getChildren("context");
			for (Element eContext:eContexts) {
				collectTypesInContext(eContext,vertices);
			}
			for (Element eContext:eContexts) {
				collectEdgesInContext(eContext,vertices,edges);
			}
			// add elements
			for (Vertex v:vertices.values()) {
				graph.addVertex(v);
			}
			for (Edge e:edges) {
				graph.addEdge(e,e.getStart(),e.getEnd());
			}
		}
		catch (Exception x) {
			throw new GraphIOException(x);
		}

		return graph;
		
	}
	private void collectTypesInContext(Element e,Map<String, Vertex> vertices) {
		List<Element> eContainers = e.getChildren("container");
		for (Element eContainer:eContainers) {
			String container = eContainer.getAttributeValue("name");
			collectTypesInContainer(eContainer,vertices,container);
		}
	}
	private void collectEdgesInContext(Element e,Map<String, Vertex> vertices,Collection<Edge> edges) {
		List<Element> eContainers = e.getChildren("container");
		for (Element eContainer:eContainers) {
			String container = eContainer.getAttributeValue("name");
			collectEdgesInContainer(eContainer,vertices,edges,container);
		}
	}
	private void collectTypesInContainer(Element e,	Map<String, Vertex> vertices, String container) {
		List<Element> eNamespaces = e.getChildren("namespace");
		for (Element eNS:eNamespaces) {
			String namespace = eNS.getAttributeValue("name");
			collectTypesInNamespace(eNS,vertices,container,namespace);
		}
	}
	private void collectEdgesInContainer(Element e,	Map<String, Vertex> vertices, Collection<Edge> edges,String container) {
		List<Element> eNamespaces = e.getChildren("namespace");
		for (Element eNS:eNamespaces) {
			String namespace = eNS.getAttributeValue("name");
			collectEdgesInNamespace(eNS,vertices,edges,container,namespace);
		}
	}
	private void collectTypesInNamespace(Element e,Map<String, Vertex> vertices, String container, String namespace) {
		List<Element> eTypes = e.getChildren("type");
		for (Element eType:eTypes) {
			String name = eType.getAttributeValue("name");
			String type = eType.getAttributeValue("classification");
			Vertex vertex = new Vertex();
			vertex.setId("v"+vertices.size());
			vertex.setType(type);
			vertex.setContainer(container);
			vertex.setNamespace(namespace);
			vertex.setName(this.getLocalName(name));
			vertices.put(name,vertex);
		}
	}
	private void collectEdgesInNamespace(Element e,Map<String, Vertex> vertices, Collection<Edge> edges, String container, String namespace) {
		List<Element> eTypes = e.getChildren("type");
		for (Element eType:eTypes) {
			String sourceType = eType.getAttributeValue("name");
			Vertex source = getOrCreateVertex(sourceType,vertices);
			Element eDependencies = eType.getChild("dependencies");
			if (eDependencies!=null) {
				List<Element> eDependsOns = eDependencies.getChildren("depends-on");
				for (Element eDependsOn:eDependsOns) {
					String depType = eDependsOn.getAttributeValue("classification");
					String targetName = eDependsOn.getAttributeValue("name");
					Vertex target = getOrCreateVertex(targetName,vertices);
					String edgeId = "edge"+edges.size();
					Edge edge = new Edge(edgeId,source,target);
					edge.setType(depType);
					edges.add(edge);
				}
			}

		}
	}
	private Vertex getOrCreateVertex(String name,Map<String, Vertex> vertices) {
		Vertex v = vertices.get(name);
		if (v==null) {
			// this is a "boundary vertex", referenced but defined outside the context
			v = new Vertex();
			v.setId("v"+vertices.size());
			v.setNamespace(this.getPackageName(name));
			v.setName(this.getLocalName(name));
			vertices.put(name,v);
		}
		return v;
	}
	private String getLocalName(String fullClassName) {
		if (fullClassName==null) return null;
		int lastDot = fullClassName.lastIndexOf('.');
		if (lastDot==-1) return fullClassName;
		else return fullClassName.substring(lastDot+1);
	}
	private String getPackageName(String fullClassName) {
		if (fullClassName==null) return null;
		int lastDot = fullClassName.lastIndexOf('.');
		if (lastDot==-1) return null;
		else return fullClassName.substring(0,lastDot);
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

}
