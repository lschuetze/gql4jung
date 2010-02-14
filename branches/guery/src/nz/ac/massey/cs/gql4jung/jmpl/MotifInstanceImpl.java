/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.jmpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Vertex;

/**
 * Motif instance implementation.
 * @author jens dietrich
 */

public class MotifInstanceImpl<V extends Vertex<E>,E extends Edge<V>> extends Logging implements MotifInstance<V,E> {
	
	private Motif motif = null;
	private Map<String,V> vertexBindings = new HashMap<String, V>();
	private Map<String,Path<V,E>> pathBindings = new HashMap<String,Path<V,E>>();
	
	MotifInstanceImpl(Motif motif,Controller<V,E> bindings) {
		this.motif = motif;
		this.vertexBindings.putAll(bindings.getRoleBindingsAsMap());
		this.pathBindings.putAll(bindings.getPathBindingsAsMap());
		if (LOG_INST.isDebugEnabled()) {
			LOG_INST.debug("result created: " + this);
		}
	}
	
	public Path<V,E> getPath(String roleName) {
		return pathBindings.get(roleName);
	}
		
	@Override
	public Motif getMotif() {
		return motif;
	}
	
	@Override
	public V getVertex(String role) {
		return vertexBindings.get(role);
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("aMotifInstance(");
		boolean f = true;
		for (Map.Entry<String,V> e:this.vertexBindings.entrySet()) {
			if (f) f=false;
			else b.append(',');
			b.append(e.getKey());
			b.append("->");
			b.append(e.getValue());
		}
		b.append(")");
		return b.toString();
	}
	
	/**
	 * Get all vertices (instantiating roles and part of paths)
	 * @return a set of vertices
	 */
	public Set<V> getVertices() {
		Set<V> vertices = new HashSet<V>();
		for (V v:this.vertexBindings.values()) {
			vertices.add(v);
		}
		for (Path<V,E> p:this.pathBindings.values()) {
			for (E e:p.getEdges()) {
				vertices.add(e.getStart());
				vertices.add(e.getEnd());
			}
		}
		return vertices;
	}
}
