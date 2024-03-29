/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung;

import java.util.Collection;
import java.util.HashSet;

/**
 * Custom vertex class.
 * @author jens dietrich
 */
public class Vertex extends GraphElement{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((container == null) ? 0 : container.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((namespace == null) ? 0 : namespace.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (container == null) {
			if (other.container != null)
				return false;
		} else if (!container.equals(other.container))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}
	// properties
	// when making this a general purpose query language, we need to remove this
	private String namespace = "";
	private String name = "";
	private boolean isAbstract = false;
	private String type = null;
	private String container = null;
	private String cluster = null;
	private String fullName = null;
	
	public Vertex(String id) {
		super(id);
	}
	public Vertex() {
		super();
	}
	private Collection<Edge> outEdges = new HashSet<Edge>();
	private Collection<Edge> inEdges = new HashSet<Edge>();
	
	public boolean isAbstract() {
		return isAbstract;
	}
	public String getType() {
		return type;
	}
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	public void setType(String type) {
		this.type = type;
	}

	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		this.fullName = null;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
		this.fullName = null;
	}
	public String getFullname() {
		if (this.fullName==null) {
			if (this.namespace==null||this.namespace.length()==0) {
				this.fullName = name;
			}
			else {
				this.fullName = namespace+'.'+name;
			}
		}
		return fullName;
	}
	public Collection<Edge> getOutEdges() {
		return outEdges;
	}
	public Collection<Edge> getInEdges() {
		return inEdges;
	}
	void addInEdge(Edge e) {
		this.inEdges.add(e);
	}
	void addOutEdge(Edge e) {
		this.outEdges.add(e);
	}
	boolean removeInEdge(Edge e) {
		return this.inEdges.remove(e);
	}
	boolean removeOutEdge(Edge e) {
		return this.outEdges.remove(e);
	}
	
	public String toString() {
		return new StringBuffer() 
			.append(this.getId())
			.append(':')
			.append(this.namespace)
			.append('.')
			.append(this.name)
			.toString();
	}
	
	public String getContainer() {
		return container;
	}
	public String getCluster() {
		return cluster;
	}
	public void setContainer(String container) {
		this.container = container;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	// checks for inner class relationships
	public boolean isPartOf(Vertex v) {
		if (!this.namespace.equals(v.namespace)) return false;
		else return (this.name.startsWith(v.name+'$'));
	}
	public void copyValuesTo(Vertex v) {
		v.setName(name);
		v.setNamespace(namespace);
		v.setContainer(container);
		v.setType(type);
		v.setCluster(cluster);
		v.setAbstract(isAbstract);
	}
}
