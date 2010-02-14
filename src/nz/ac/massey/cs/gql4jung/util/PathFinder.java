/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.util;

import java.util.*;

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;

import com.google.common.base.Predicate;

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Utility to find paths in graphs.
 * TODO: check for infinite loops if computeAll is set to true
 * @author jens dietrich
 */
public class PathFinder {
	
	/**
	 * Find the links starting // ending at a vertex.
	 * The iterator will return the paths ordered, shorter paths are visited first.
	 * @param g
	 * @param start
	 * @param minLength
	 * @param maxLength
	 * @param outgoing the direction
	 * @param filter
	 * @return
	 */
	public  static <V extends Vertex<E>,E extends Edge<V>> Iterator<Path<V,E>> findLinks(DirectedGraph<V,E> g,V start, int minLength, int maxLength, boolean outgoing, Predicate<E> filter,boolean computeAll) {
		// try cache first
		//List<Path> coll = PathCache.INSTANCE.get(g, start, minLength, maxLength, outgoing, filter);
		//if (coll!=null) return coll.iterator();
		
		// we pre init the iterator, this should be done on demand
		List<Path<V,E>> coll = new ArrayList<Path<V,E>> ();
		Map<V,Object> visited = new IdentityHashMap<V,Object>();
		List<Path<V,E>> layer = new ArrayList<Path<V,E>>();
		
		Path<V,E> initialPath = new Path<V,E>(start);
		layer.add(initialPath);
		if (minLength==0) {
			coll.add(initialPath);
			visited.put(start,null);
		}
		
		collectPaths(1,coll,visited,layer,start,minLength,maxLength,outgoing,filter,computeAll);
		// cache
		//PathCache.INSTANCE.put(g,start, minLength, maxLength, outgoing, filter, coll);
		
		return coll.iterator();
		
	}
	private static <V extends Vertex<E>,E extends Edge<V>> void collectPaths(int i, Collection<Path<V,E>> coll,Map<V, Object> visited, Collection<Path<V,E>> lastLayer,V start, int minLength, int maxLength, boolean outgoing, Predicate<E> filter,boolean computeAll) {
		if (maxLength!=-1 && i>maxLength) return ; // do not continue
		Collection<Path<V,E>> nextLayer = new ArrayList<Path<V,E>>();
		boolean hasNewVertices = false;
		for (Path<V,E> edges:lastLayer) {
			V end = getEnd(edges,outgoing);
			Collection<E> edges1 = getNext(end,outgoing);
			for (E edge:edges1) {
				if (filter==null || filter.apply(edge)) {
					V newEnd = getEndPoint(edge,outgoing);
					if (computeAll || !visited.containsKey(newEnd)) {
						// new node, not yet seen
						hasNewVertices = true;
						visited.put(newEnd, null);
						// if min length is ok, add this
						Path<V,E> newPath = outgoing?edges.addAtEnd(edge):edges.addAtStart(edge);
						nextLayer.add(newPath);
						if (i>=minLength) {
							coll.add(newPath);
						}
					}
				}
			}
		}
		if (hasNewVertices) {
			collectPaths(i+1, coll, visited, nextLayer,start,minLength,maxLength,outgoing,filter,computeAll);
		}

	}
	private static <V extends Vertex<E>,E extends Edge<V>> V getEndPoint(E edge,boolean outgoing) {
		if (outgoing) return (V) edge.getEnd();
		else return (V) edge.getStart();
	}
	private static <V extends Vertex<E>,E extends Edge<V>> Collection<E> getNext(V v,boolean outgoing) {
		return outgoing?v.getOutEdges():v.getInEdges();
	}
	private static <V extends Vertex<E>,E extends Edge<V>> V getEnd(Path<V,E> path,boolean outgoing) {
		if (outgoing) return path.getEnd();
		else return path.getStart();
	}

}
