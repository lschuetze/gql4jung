/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.impl;

import java.util.List;
import nz.ac.massey.cs.gql4jung.Constraint;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;

/**
 * Utility class to control backtracking.
 * @author jens dietrich
 */
class Controller<V extends Vertex<E>,E extends Edge<V>>  extends Logging {
	public static int SIZE = 100;
	private String[] keys4roles = new String[SIZE];
	private V[] values4roles = (V[]) new Vertex[SIZE];
	private String[] keys4links = new String[SIZE];
	private Path<V,E>[] values4links = new Path[SIZE];
	protected List<Constraint> constraints = null;
	protected int position = 0;

	/**
	 * Constructor.
	 * @param motif the query
	 * @param constraints the constraints (perhaps already arranged by an optimizer)
	 */
	public Controller(Motif motif,List<Constraint> constraints) {
		super();
		this.constraints = constraints;
	}

	/**
	 * Lookup the binding for a given key.
	 */
	public V lookupVertex(String k) {
		for (int i=position;i>-1;i--) {
			if (k.equals(keys4roles[i])) 
				return values4roles[i];
		}
		return null;
	}
	/**
	 * Lookup the binding for a given key.
	 */
	public Path<V,E> lookupPath(String k) {
		for (int i=position;i>-1;i--) {
			if (k.equals(keys4links[i])) 
				return values4links[i];
		}
		return null;
	}
	/**
	 * Lookup the binding for a given key, return a vertex or a path
	 */
	public Object lookupAny(String k) {
		Object o = this.lookupVertex(k);
		if (o!=null) return o;
		else return this.lookupPath(k);
	}

	/**
	 * Add a new entry.
	 */
	public void bind(String k,V v) {
		assert(k!=null);
		assert(v!=null);		
		keys4roles[position]= k;
		values4roles[position] = v;
		if (LOG_BIND.isDebugEnabled()) {
			StringBuffer b = new StringBuffer();
			b.append("binding ");
			b.append(k);
			b.append(" -> ");
			b.append(v.toString());
			//LOG_BIND.debug("binding "+k+" -> "+v);
			LOG_BIND.debug(b.toString());
		}
	}

	/**
	 * Add a new entry.
	 */
	public void bind(String k,Path<V,E> p) {
		assert(k!=null);
		assert(p!=null);		
		keys4links[position]= k;
		values4links[position] = p;
		//System.out.println("binding "+k+" -> "+v);
	}
	/**
	 * Indicates whether the values contain a given value.
	 * @deprecated
	 */
	public boolean containsValue(String role) {
		for (int i=position;i>-1;i--) {
			if (role.equals(values4roles[i])) 
				return true;
		}
		return false;
	}
	/**
	 * Converts the role bindings to a map.
	 */
	public java.util.Map<String,V> getRoleBindingsAsMap() {
		java.util.Map<String,V> map = new java.util.Hashtable<String,V>();
		for (int i=position;i>-1;i--) {
			if (keys4roles[i]!=null)
				map.put(keys4roles[i],values4roles[i]);
		}
		return map;
	}
	public java.util.Map<String,Path<V,E>> getPathBindingsAsMap() {
		java.util.Map<String,Path<V,E>> map = new java.util.Hashtable<String,Path<V,E>>();
		for (int i=position;i>-1;i--) {
			if (keys4links[i]!=null)
				map.put(keys4links[i],values4links[i]);
		}
		return map;
	}

	/**
	 * Goes one level up.
	 */
	public void backtrack() {
		this.keys4roles[position]=null;
		this.values4roles[position]=null;
		this.keys4links[position]=null;
		this.values4links[position]=null;
		position = position-1;
		if (LOG_BIND.isDebugEnabled()) {
			LOG_BIND.debug("backtracking to "+position);
		}		
	}
	public void reset() {
		keys4roles = new String[SIZE];
		values4roles = (V[]) new Vertex[SIZE];
		keys4links = new String[SIZE];
		values4links = new Path[SIZE];
		position = 0;
	}
	
	/**
	 * Get the value at a certain position.
	 * Useful for debugging.
	 * @param pos a position
	 * @return a value
	 */
	 public V getValue(int pos) {
		return pos<SIZE?this.values4roles[pos]:null; 
	 }
	/**
	 * Get the key at a certain position.
	 * Useful for debugging.
	 * @param pos a position
	 * @return a key
	 */
	 public String getKey(int pos) {
		return pos<SIZE?this.keys4roles[pos]:null; 
	 }
	 /**
	  * Get the number of bindings.
	  * @return the position
	  */
	 public int getSize() {
		 return position+(keys4roles[position]==null?0:1);
	 }
	 /**
	  * Get the position.
	  */
	 public int getPosition() {
		 return this.position;
	 }
	 
	public boolean isDone() {
		return position==constraints.size();
	}

	public Constraint next() {
		Constraint c = constraints.get(position);
		position=position+1;
		return c;
	}

	public boolean isInJumpBackMode() {
		return false;
	}
}
