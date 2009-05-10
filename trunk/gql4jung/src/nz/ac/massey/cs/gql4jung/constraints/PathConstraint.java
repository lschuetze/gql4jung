/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.constraints;

import java.rmi.server.UID;
import java.util.Iterator;
import java.util.List;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import nz.ac.massey.cs.gql4jung.ConnectedVertex;
import nz.ac.massey.cs.gql4jung.LinkConstraint;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.jmpl.PathImpl;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
/**
 * Constraint to check the existence of paths between nodes.
 * @author jens.dietrich@gmail.com
 *
 */
public class PathConstraint extends LinkConstraint<Path> {
	private int maxLength = -1; // this means unbound	
	private int minLength = 1;
	//private String id = null;
	
	public PathConstraint() {
		super();
		UID pathID = new UID();
		super.id = pathID.toString();
	}
		
	public Iterator<ConnectedVertex<Path>> getPossibleSources(final Graph g,final Vertex target) {

		Predicate<Edge> filter = new Predicate<Edge>() {
			@Override
			public boolean apply(Edge e) {
				if (edgePropertyConstraint==null) return true;
				else return edgePropertyConstraint.check(g,e);
			}
		};
		NodeIterator sources = new NodeIterator(target,this.minLength,this.maxLength, false,filter);

		// vertex to path transformer
		Function<List<Edge>,ConnectedVertex<Path>> transformer = new Function<List<Edge>,ConnectedVertex<Path>>() {
			@Override
			public ConnectedVertex<Path> apply(List<Edge> edges) {
				PathImpl path = new PathImpl();
				path.setEdges(edges);
				if (edges.size()==0) {
					path.setStart(target);
					path.setEnd(target);
				}
				else {
					path.setStart((Vertex) edges.get(0).getEndpoints().getFirst());
					path.setEnd(target);
				}
				
				ConnectedVertex<Path> p = new ConnectedVertex<Path>(path,path.getStart());
				return p;
			}
		};
		
		return Iterators.transform(sources.iterator(),transformer);
	}
	public Iterator<ConnectedVertex<Path>>  getPossibleTargets(final Graph g, final Vertex source){

		Predicate<Edge> filter = new Predicate<Edge>() {
			@Override
			public boolean apply(Edge e) {
				if (edgePropertyConstraint==null) return true;
				else return edgePropertyConstraint.check(g,e);
			}
		};
		NodeIterator sources = new NodeIterator(source,this.minLength,this.maxLength, true, filter);

		// vertex to path transformer
		Function<List<Edge>,ConnectedVertex<Path>> transformer = new Function<List<Edge>,ConnectedVertex<Path>>() {
			@Override
			public ConnectedVertex<Path> apply(List<Edge> edges) {
				PathImpl path = new PathImpl();
				path.setEdges(edges);
				if (edges.size()==0) {
					path.setStart(source);
					path.setEnd(source);
				}
				else {
					path.setStart(source);
					path.setEnd((Vertex) edges.get(edges.size()-1).getEndpoints().getSecond());
				}
				
				ConnectedVertex<Path> p = new ConnectedVertex<Path>(path,path.getEnd());
				return p;
			}
		};
		
		return Iterators.transform(sources.iterator(),transformer);
	}
	private boolean checkPath(Graph g,List<Edge> path) {
		boolean lengthOK =  path.size()!=0 && (maxLength==-1 || path.size()<maxLength);
		if (lengthOK) {
			for (Edge e:path) {
				if (!edgePropertyConstraint.check(g,e)) return false;
			}
		};
		return false;
	}
	public Path check(final Graph g, final Vertex source, final Vertex target){
		
		Predicate<Edge> filter = new Predicate<Edge>() {
			@Override
			public boolean apply(Edge e) {
				if (edgePropertyConstraint==null) return true;
				else return edgePropertyConstraint.check(g,e);
			}
		};
		NodeIterator links = new NodeIterator(source,this.minLength,this.maxLength, true, filter);
		links.iterator();
		for (List<Edge> link:links) {
			if ((link.size()==0 && source==target) || target==link.get(link.size()-1).getEndpoints().getSecond()) {
				PathImpl pp = new PathImpl();
				pp.setEdges(link);
				pp.setStart(source);
				pp.setEnd(target);
				return pp;
			}
		}
		return null;
	}

	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
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

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}


}

