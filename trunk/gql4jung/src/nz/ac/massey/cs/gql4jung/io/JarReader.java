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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jeantessier.classreader.Classfile;
import com.jeantessier.classreader.ClassfileLoader;
import com.jeantessier.classreader.LoadEvent;
import com.jeantessier.classreader.LoadListener;
import com.jeantessier.classreader.TransientClassfileLoader;
import com.jeantessier.dependency.ClassNode;
import com.jeantessier.dependency.CodeDependencyCollector;
import com.jeantessier.dependency.ComprehensiveSelectionCriteria;
import com.jeantessier.dependency.DependencyEvent;
import com.jeantessier.dependency.DependencyListener;
import com.jeantessier.dependency.FeatureNode;
import com.jeantessier.dependency.Node;
import com.jeantessier.dependency.NodeFactory;
import com.jeantessier.dependency.SelectionCriteria;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Vertex;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphIOException;

/**
 * Reads the graph from jar files, used depfind
 * (http://depfind.sourceforge.net/).
 * 
 * @author jens dietrich
 */
public class JarReader {

	private File[] jars = null;
	private boolean removeDuplicateEdges = true;
	private boolean removeSelfRefs = true;

	public JarReader(File[] jars) {
		super();
		this.jars = jars;
	}
	
	public boolean isRemoveDuplicateEdges() {
		return removeDuplicateEdges;
	}
	public void setRemoveDuplicateEdges(boolean removeDuplicateEdges) {
		this.removeDuplicateEdges = removeDuplicateEdges;
	}
	public boolean isRemoveSelfRefs() {
		return removeSelfRefs;
	}
	public void setRemoveSelfRefs(boolean removeSelfRefs) {
		this.removeSelfRefs = removeSelfRefs;
	}

	public synchronized DirectedGraph<Vertex, Edge> readGraph()	throws GraphIOException {
		NodeFactory factory = new NodeFactory();
		SelectionCriteria filter = new ComprehensiveSelectionCriteria();
		CodeDependencyCollector collector = new CodeDependencyCollector(factory);
		ClassfileLoader loader = new TransientClassfileLoader();
		List<String> list = new ArrayList<String>();
		for (File f:jars) {
			list.add(f.getAbsolutePath());
		}
		final List<Classfile> classfiles = new ArrayList<Classfile>();
		
		final DirectedGraph<Vertex, Edge> graph = new DirectedSparseGraph<Vertex, Edge> ();
		final Map<String,Vertex> vertices = new HashMap<String,Vertex>();
		
		loader.addLoadListener(new LoadListener() {
			@Override
			public void beginClassfile(LoadEvent event) {}
			@Override
			public void beginFile(LoadEvent event) {}
			@Override
			public void beginGroup(LoadEvent event) {
				System.out.println("start library " + event.getGroupName());
			}
			@Override
			public void beginSession(LoadEvent event) {
				System.out.println("start depfind session");
			}
			@Override
			public void endClassfile(LoadEvent event) {
				classfiles.add(event.getClassfile());
			}
			@Override
			public void endFile(LoadEvent event) {}
			@Override
			public void endGroup(LoadEvent event) {
				System.out.println("finish library " + event.getGroupName());
			}
			@Override
			public void endSession(LoadEvent event) {
				System.out.println("end depfind session");
			}
		});
		loader.load(list);
		int counter = 1;
		for (Classfile classfile:classfiles) {
			addVertex(graph,classfile,counter,vertices);
			counter = counter+1;
		}
		
		collector.addDependencyListener(new DependencyListener() {
			int counter = 0;
			@Override
			public void beginClass(DependencyEvent event) {}

			@Override
			public void beginSession(DependencyEvent event) {}

			@Override
			public void dependency(DependencyEvent event) {
				Node start = event.getDependent();
				Node end = event.getDependable();
				if (start instanceof ClassNode && end instanceof ClassNode) {
					addEdge(graph,start,end,false,counter,vertices);
					counter = counter+1;
				}
				else {
					addEdge(graph,start,end,true,counter,vertices);
					counter = counter+1;
				}
			}

			@Override
			public void endClass(DependencyEvent event) {}

			@Override
			public void endSession(DependencyEvent event) {}
		});
		System.out.println("class loaded: " + loader.getAllClassfiles().size());
		for (Classfile cf : classfiles) {
			collector.visitClassfile(cf);
		}

		return graph;

	}

	private void addVertex(DirectedGraph<Vertex, Edge> graph,Classfile classfile,int id,Map<String,Vertex> vertices) {
		Vertex v = new Vertex();
		v.setId(String.valueOf(id));
		v.setName(classfile.getSimpleName());
		v.setAbstract(classfile.isAbstract());
		v.setNamespace(classfile.getClassName().substring(0,classfile.getClassName().lastIndexOf('.')));
		v.setType(getType(classfile));
		v.setContainer("nyi"); // not yet supported
		graph.addVertex(v);
		vertices.put(classfile.getClassName(),v);
		
		System.out.println("Adding vertex " + v);
	}
	
	private void addEdge(DirectedGraph<Vertex, Edge> graph, Node start,Node end, boolean isUses, int counter, Map<String, Vertex> vertices) {
		Vertex source = findVertex(start,vertices);
		Vertex target = findVertex(end,vertices);
		
		if (target!=null) { // this is possible - reference to external class
			String type = null;
			if (isUses) {
				type = "uses";
			}
			else {
				if ("class".equals(source.getType()) && "interface".equals(target.getType())) {
					type = "implements";
				}
				else {
					type = "extends";
				}
			}
			Edge edge = new Edge();
			edge.setId("e-"+counter);
			edge.setStart(source);
			edge.setEnd(target);
			edge.setType(type);
			boolean addEdge = true;
			if (this.removeSelfRefs && source==target) {
				addEdge = false;
			}
			if (addEdge && this.removeDuplicateEdges) {
				for (Edge e:graph.getOutEdges(source)) {
					if (e.getEnd()==target && e.getType()==type) {
						addEdge=false;
						break;
					}
				}
			}
			if (addEdge) {
				graph.addEdge(edge,source,target);
				System.out.println("Adding edge " + edge);
			}
			
			
		}
	}

	private Vertex findVertex(Node n, Map<String, Vertex> vertices) {
		ClassNode cNode = null;
		if (n instanceof ClassNode) {
			cNode = (ClassNode)n;
		}
		else if (n instanceof FeatureNode) {
			FeatureNode fNode = (FeatureNode)n;
			cNode = fNode.getClassNode();
		}
		if (cNode!=null) {
			return vertices.get(cNode.getName());
		}
		else {
			return null;
		}
	}

	private String getType(Classfile classfile) {
		if (classfile.isInterface()) return "interface";
		else if (classfile.isEnum()) return "enum";
		else if (classfile.isAnnotation()) return "annotation";
		return "class";
	}

	public void close() {
	}

}
