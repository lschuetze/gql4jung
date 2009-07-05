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

import org.apache.log4j.Logger;

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

	private List<File> jars = null;
	private boolean removeDuplicateEdges = true;
	private boolean removeSelfRefs = true;

	public JarReader(List<File> jars) {
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
		// TODO - for now we remove all appenders
		// need to add proper log4j initialisation later
		// comprehensive logging leads to memory problems
		Logger.getRootLogger().removeAllAppenders();
		
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
		final Map<Classfile,String> containerMapping = new HashMap<Classfile,String>();
		
		loader.addLoadListener(new LoadListener() {
			String container = null;
			@Override
			public void beginClassfile(LoadEvent event) {}
			@Override
			public void beginFile(LoadEvent event) {
				
			}
			@Override
			public void beginGroup(LoadEvent event) {
				String name = event.getGroupName();
				try {
					File f = new File(name);
					if (f.exists()) {
						container = f.getName();
					}
				}
				catch (Exception x){}
			}
			@Override
			public void beginSession(LoadEvent event) {
				//System.out.println("start depfind session ");
			}
			@Override
			public void endClassfile(LoadEvent event) {
				Classfile cf = event.getClassfile();
				classfiles.add(cf);
				containerMapping.put(cf,container);
			}
			@Override
			public void endFile(LoadEvent event) {}
			@Override
			public void endGroup(LoadEvent event) {}
			@Override
			public void endSession(LoadEvent event) {}
		});
		loader.load(list);
		int counter = 1;
		for (Classfile classfile:classfiles) {
			addVertex(graph,classfile,counter,vertices,containerMapping);
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
		//System.out.println("class loaded: " + loader.getAllClassfiles().size());
		for (Classfile cf : classfiles) {
			collector.visitClassfile(cf);
		}

		return graph;

	}

	private void addVertex(DirectedGraph<Vertex, Edge> graph,Classfile classfile,int id,Map<String,Vertex> vertices,Map<Classfile,String> containerMapping) {
		Vertex v = new Vertex();
		v.setId(String.valueOf(id));
		v.setName(classfile.getSimpleName());
		v.setAbstract(classfile.isAbstract());
		v.setNamespace(classfile.getClassName().substring(0,classfile.getClassName().lastIndexOf('.')));
		v.setType(getType(classfile));
		v.setContainer(containerMapping.get(classfile)); // not yet supported
		graph.addVertex(v);
		vertices.put(classfile.getClassName(),v);
		
		//System.out.println("Adding vertex " + v);
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

			boolean addEdge = true;
			if (this.removeSelfRefs && source==target) {
				addEdge = false;
			}
			if (addEdge && this.removeDuplicateEdges) {
				for (Edge e:graph.getOutEdges(source)) {
					// TODO, FIXME
					// note that jung will not allow to add another edge with the same sourec or target
					// this means, we cannot have two edges of different types (extends and uses)
					// however, this is sometimes interesting, e.g. in the composite pattern
					// solution: use flags instead of a type attribute in Vertex
					if (e.getEnd()==target) {
						addEdge=false;
						break;
					}
				}
			}
			if (addEdge) {
				Edge edge = new Edge();
				edge.setId("e-"+counter);
				edge.setStart(source);
				edge.setEnd(target);
				edge.setType(type);
				boolean added = graph.addEdge(edge,source,target);
				/** log for debugging
				if (start.toString().indexOf("org.apache.log4j.jdbc.JDBCAppender")>-1) {
					System.out.println("Adding edge " + edge + (added?"success":"failed"));
					System.out.println("  Logging outgoing edges: ");
					for (Edge e:source.getOutEdges()) {
						System.out.println("  - "+e);
					}

				}
				if (!added) {
					System.out.println("Rejected edge " + edge);
				}
				*/

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
