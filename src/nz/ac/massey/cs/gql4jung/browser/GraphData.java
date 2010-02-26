/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.browser;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;

import org.apache.log4j.Logger;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Bean representing graph properties.
 * @author Jens Dietrich
 */
public class GraphData implements PropertyBean {
	
	private DirectedGraph<TypeNode,TypeReference> graph = null;
	
	private int vertexCount = 0;
	private int edgeCount = 0;
	private int containerCount = 0;
	private int namespaceCount = 0;
	private int extendsCount = 0;
	private int usesCount = 0;
	private int implementsCount = 0;
	private int classCount = 0;
	private int interfaceCount = 0;
	private int enumCount = 0;
	private int annotationCount = 0;
	private String containers = null; // comma separated
	
	public DirectedGraph<TypeNode,TypeReference> getGraph() {
		return graph;
	}

	
	public GraphData(DirectedGraph<TypeNode,TypeReference> graph) {
		super();
		this.graph = graph;
		
		// compute
		vertexCount = this.graph.getVertexCount();
		edgeCount = this.graph.getEdgeCount();
		
		Set<String> containers = new HashSet<String>();
		Set<String> namespaces = new HashSet<String>();
		
		for (TypeNode v:graph.getVertices()) {
			containers.add(v.getContainer());
			namespaces.add(v.getNamespace());
			String type = v.getType();
			classCount = classCount + ("class".equals(type)?1:0);
			interfaceCount = interfaceCount + ("interface".equals(type)?1:0);
			enumCount = enumCount + ("enum".equals(type)?1:0);
			annotationCount = annotationCount + ("annotation".equals(type)?1:0);
		}
		this.containerCount = containers.size();
		this.namespaceCount = namespaces.size();
		this.containers = printList(containers);
		for (TypeReference e:graph.getEdges()) {
			String type = e.getType();
			usesCount = usesCount + ("uses".equals(type)?1:0);
			extendsCount = extendsCount + ("extends".equals(type)?1:0);
			implementsCount = implementsCount + ("implements".equals(type)?1:0);
		}

	}
	private String printList(Collection<String> container) {
		StringBuffer b = new StringBuffer();
		boolean f = true;
		for (String t:container) {
			if (f) f = false;
			else b.append(',');
			b.append(t);
		}
		return b.toString();
	}


	public int getVertexCount() {
		return vertexCount;
	}

	public int getEdgeCount() {
		return edgeCount;
	}
	public int getContainerCount() {
		return containerCount;
	}
	public int getNamespaceCount() {
		return namespaceCount;
	}
	public int getExtendsCount() {
		return extendsCount;
	}
	public int getUsesCount() {
		return usesCount;
	}
	public int getImplementsCount() {
		return implementsCount;
	}
	public int getClassCount() {
		return classCount;
	}
	public int getInterfaceCount() {
		return interfaceCount;
	}
	public int getEnumCount() {
		return enumCount;
	}
	public int getAnnotationCount() {
		return annotationCount;
	}
	public String getContainers() {
		return containers;
	}

	@Override
	public void reset() {
		// nothing to do here - read only
	}
	@Override
	public void save() throws IOException {
		// nothing to do here - read only
	}


	@Override
	public PropertyDescriptor[] getProperties() {
		try {
			PropertyDescriptor[] properties = {
				new PropertyDescriptor("vertex count",GraphData.class,"getVertexCount",null),
				new PropertyDescriptor("edge count",GraphData.class,"getEdgeCount",null),
				new PropertyDescriptor("container count",GraphData.class,"getContainerCount",null),
				new PropertyDescriptor("containers",GraphData.class,"getContainers",null),
				new PropertyDescriptor("namespace count",GraphData.class,"getNamespaceCount",null),
				new PropertyDescriptor("extends relationship count",GraphData.class,"getExtendsCount",null),
				new PropertyDescriptor("uses relationship count",GraphData.class,"getUsesCount",null),
				new PropertyDescriptor("implements relationship count",GraphData.class,"getImplementsCount",null),
				new PropertyDescriptor("class count",GraphData.class,"getClassCount",null),
				new PropertyDescriptor("interface count",GraphData.class,"getInterfaceCount",null),
				new PropertyDescriptor("enum count",GraphData.class,"getEnumCount",null),
				new PropertyDescriptor("annotation count",GraphData.class,"getAnnotationCount",null)				
			};	
			return properties;
		}
		catch (Exception x) {
			Logger.getLogger(this.getClass()).error("Exception initializing settings",x);
			return new PropertyDescriptor[0];
		}
	}


}
