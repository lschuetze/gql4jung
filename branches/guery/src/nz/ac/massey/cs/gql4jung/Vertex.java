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
 * General vertex class. Vertices have references to incoming and outgoing edges. This is different to jung 2. 
 * The advantage is that lookup is much faster.
 * @author jens dietrich
 */
public class Vertex<E extends Edge> extends GraphElement{

	
	public Vertex(String id) {
		super(id);
	}
	public Vertex() {
		super();
	}
	private Collection<E> outEdges = new HashSet<E>();
	private Collection<E> inEdges = new HashSet<E>();
	
	
	public Collection<E> getOutEdges() {
		return outEdges;
	}
	public Collection<E> getInEdges() {
		return inEdges;
	}
	void addInEdge(E e) {
		this.inEdges.add(e);
	}
	void addOutEdge(E e) {
		this.outEdges.add(e);
	}
	boolean removeInEdge(E e) {
		return this.inEdges.remove(e);
	}
	boolean removeOutEdge(E e) {
		return this.outEdges.remove(e);
	}
	
}
