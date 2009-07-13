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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import nz.ac.massey.cs.gql4jung.util.ConstraintedShortestPathFinder;
import nz.ac.massey.cs.gql4jung.util.PathFinder;
import edu.uci.ics.jung.graph.DirectedGraph;
/**
 * Constraint to check the existence of paths between nodes.
 * @author jens dietrich
 */
public class PathConstraint implements Constraint {
	
	private Predicate<Edge> filter = new Predicate<Edge>() {
		@Override
		public boolean apply(Edge e) {
			for (PropertyConstraint constraint:constraints) {
				if (!constraint.check(e)) return false;
			}
			return true;
		}
	};
	
	private int maxLength = -1; // this means unbound	
	private int minLength = 1;
	private String role = null;
	private String source = null;
	private String target = null;
	private boolean computeAll = false; // whether to compute only one instance or all


	private List<PropertyConstraint> constraints = new ArrayList<PropertyConstraint>();
	

	
	public PathConstraint() {
		super();
	}
		
	public Iterator<Path> getPossibleSources(final DirectedGraph<Vertex,Edge> g,final Vertex target) {
		return PathFinder.findLinks(g,target,this.minLength,this.maxLength, false,filter,computeAll);
	}
	public Iterator<Path>  getPossibleTargets(final DirectedGraph<Vertex,Edge> g, final Vertex source){
		return PathFinder.findLinks(g,source,this.minLength,this.maxLength, true, filter,computeAll);
	}

	public Iterator<Path> check(final DirectedGraph<Vertex,Edge> g, final Vertex source, final Vertex target){
		if (this.computeAll) {
			Iterator<Path> allOutgoing = PathFinder.findLinks(g,source,this.minLength,this.maxLength, true, filter,computeAll);
			Predicate<Path> connectToTargetFilter = new Predicate<Path> () {
				@Override
				public boolean apply(Path p) {
					return p.getEnd()==target;
				}
			};
			return Iterators.filter(allOutgoing, connectToTargetFilter);
		}
		else {
			Path path = ConstraintedShortestPathFinder.findLink(g,source,target,minLength,maxLength,filter);
			if (path==null) {
				return Iterators.emptyIterator();
			}
			else {
				return Iterators.singletonIterator(path); 
			}
		}
	}


	public String toString() {
		return new StringBuffer()
			.append("path constraint[")
			.append(this.getSource())
			.append("->")
			.append(this.getTarget())
			.append("]")
			.toString();
	}

	public int getMaxLength() {
		return maxLength;
	}

	public int getMinLength() {
		return minLength;
	}

	public String getRole() {
		return role;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void addConstraint(PropertyConstraint constraint) {
		this.constraints.add(constraint);
	}

	public boolean isComputeAll() {
		return computeAll;
	}

	public void setComputeAll(boolean computeAll) {
		this.computeAll = computeAll;
	}

}

