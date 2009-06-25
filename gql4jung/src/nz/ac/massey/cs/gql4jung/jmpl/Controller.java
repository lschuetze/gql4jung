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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import nz.ac.massey.cs.gql4jung.Constraint;
import nz.ac.massey.cs.gql4jung.GroupByClause;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;

/**
 * Data structure for variable bindings.
 * @author jens dietrich
 */
class Controller  extends Logging {
	public static int SIZE = 100;
	private String[] keys4roles = new String[SIZE];
	private Vertex[] values4roles = new Vertex[SIZE];
	private String[] keys4links = new String[SIZE];
	private Path[] values4links = new Path[SIZE];
	private int jumpBackPosition = -1; // jump back for aggregation mode
	private boolean jumpBackMode = false; // jump back for aggregation mode
	private Collection<String> aggregationRoles = null;
	private List<Constraint> constraints = null;
	private List<GroupByClause> groupByClauses = null;
	private Collection<Object> resultCores = null; // used to check aggregation
	
	private int position = 0;

	/**
	 * Constructor.
	 * @param motif Bindings are in aggregation mode iff motif!=null.
	 */
	public Controller(Motif motif,List<Constraint> constraints,boolean jumpBackMode) {
		super();
		this.constraints = constraints;
		
		if (jumpBackMode) {
			aggregationRoles = new HashSet<String>();
			groupByClauses = new ArrayList<GroupByClause>();
			resultCores = new HashSet<Object>();
			for (GroupByClause gb:motif.getGroupByClauses()) {
				aggregationRoles.add(gb.getRole());
				groupByClauses.add(gb);
			}
		}
		
	}
	private boolean isInAggregationMode() {
		return aggregationRoles!=null;
	}
	/**
	 * Lookup the binding for a given key.
	 */
	public Vertex lookup(String k) {
		for (int i=position;i>-1;i--) {
			if (k.equals(keys4roles[i])) 
				return values4roles[i];
		}
		return null;
	}
	/**
	 * Add a new entry.
	 */
	public void bind(String k,Vertex v) {
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
		// compute initial jumpback position
		if (jumpBackPosition==-1&&isInAggregationMode()) {
			this.aggregationRoles.remove(k);
			if (this.aggregationRoles.size()==0) {
				setBackjumpPosition(this.getPosition());
			}
		}
		// check if we have to back into jumpback mode
		if (isInAggregationMode()&&this.position==jumpBackPosition) {
			Object core = this.createResultCore();
			if (core!=null && this.resultCores.contains(core)) {
				this.jumpBackMode=true;
				if (LOG_BIND.isDebugEnabled()) {
					LOG_BIND.debug("going back into jumpback mode at position "+position);
				}
			}
		}
	}
	private void setBackjumpPosition(int pos) {
		this.jumpBackPosition = pos;
		if (LOG_BACKJUMP.isDebugEnabled()) {
			LOG_BACKJUMP.debug("jump back position set to " + pos);
		}		
	}
	/**
	 * Add a new entry.
	 */
	public void bind(String k,Path p) {
		assert(k!=null);
		assert(p!=null);		
		keys4links[position]= k;
		values4links[position] = p;
		//System.out.println("binding "+k+" -> "+v);
	}
	/**
	 * Indicates whether the values contain a given value.
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
	public java.util.Map<String,Vertex> getRoleBindingsAsMap() {
		java.util.Map<String,Vertex> map = new java.util.Hashtable<String,Vertex>();
		for (int i=position;i>-1;i--) {
			if (keys4roles[i]!=null)
				map.put(keys4roles[i],values4roles[i]);
		}
		return map;
	}
	public java.util.Map<String,Path> getPathBindingsAsMap() {
		java.util.Map<String,Path> map = new java.util.Hashtable<String,Path>();
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
		if (this.jumpBackMode && this.position<=this.jumpBackPosition) {
			this.jumpBackMode=false;
			/**
			// also check existing solutions here
			List<Object> core = this.createResultCore();
			if (core==null || !this.resultCores.contains(core)) {
				this.jumpBackMode=false;
				if (LOG_BIND.isDebugEnabled()) {
					LOG_BIND.debug("leaving jump back mode at position "+position);
				}
			}
			*/
		}
	}
	public void reset() {
		keys4roles = new String[SIZE];
		values4roles = new Vertex[SIZE];
		keys4links = new String[SIZE];
		values4links = new Path[SIZE];
		position = 0;
		this.jumpBackMode = false;
	}
	
	/**
	 * Get the value at a certain position.
	 * Useful for debugging.
	 * @param pos a position
	 * @return a value
	 */
	 public Vertex getValue(int pos) {
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
	 
	boolean isDone() {
		boolean done =  position==constraints.size();
		if (done && isInAggregationMode()) {
			// build new result core
			Object core = this.createResultCore();
			this.resultCores.add(core);
			this.jumpBackMode = true;
		}
		return done;
	}
	private Object createResultCore() {
		if (groupByClauses.size()==1) {
			GroupByClause gb = groupByClauses.get(0);
			Vertex v = this.lookup(gb.getRole());
			if (v==null) return null; // not enough bindings to build core
			return gb.getGroup(v);
		}
		List<Object> core = new ArrayList<Object>(groupByClauses.size());
		for (GroupByClause gb:groupByClauses) {
			Vertex v = this.lookup(gb.getRole());
			if (v==null) return null; // not enough bindings to build core
			Object group = gb.getGroup(v);
			core.add(group);
		}
		return core;
	}
	public Constraint next() {
		Constraint c = constraints.get(position);
		position=position+1;
		return c;
	}
	public boolean isInJumpBackMode() {
		return jumpBackMode;
	}
	public int getJumpBackPosition() {
		return this.jumpBackPosition;
	}
}
