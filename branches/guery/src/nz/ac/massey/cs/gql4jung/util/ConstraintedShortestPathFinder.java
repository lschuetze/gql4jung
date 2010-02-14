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
import nz.ac.massey.cs.gql4jung.*;

import com.google.common.base.Predicate;

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Utility to find the shortest path satisfying certain constraints (length and predicates on edges)
 * @author jens dietrich
 */
public class ConstraintedShortestPathFinder  {
	
	public  static <V extends Vertex<E>,E extends Edge<V>> Path<V,E> findLink(DirectedGraph<V,E> g,V start, V target, int minLength, int maxLength, Predicate<E> filter) {
		Map<V,Object> visited = new IdentityHashMap<V,Object>();
		Collection<Path<V,E>> layer = new ArrayList<Path<V,E>>();
		
		Path<V,E> initialPath = new Path<V,E>(start);
		if (minLength==0 && start==target) {
			return initialPath;
		}
		
		layer.add(initialPath);		
		return find(layer,visited,1,target,minLength,maxLength,filter);		
	}
	
	
	private static <V extends Vertex<E>,E extends Edge<V>>  Path<V,E> find(Collection<Path<V,E>> lastLayer,Map<V,Object> visited,int level, V target, int minLength, int maxLength, Predicate<E> filter) {
		// path is too long, give up
		if (maxLength!=-1 && level>maxLength) return null;
		// nothing more todo
		if (lastLayer.isEmpty()) return null;
		
		Collection<Path<V,E>> nextLayer = new ArrayList<Path<V,E>>();
		for (Path<V,E> path:lastLayer) {
			V end = path.getEnd();
			Collection<E> outEdges = end.getOutEdges();
			for (E nextEdge:outEdges) {
				if (filter.apply(nextEdge)) {
					V outEnd = (V)nextEdge.getEnd();
					if (outEnd==target && level>=minLength) {
						return path.addAtEnd(nextEdge);
					}
					else if (!visited.containsKey(outEnd)){
						nextLayer.add(path.addAtEnd(nextEdge)); 
						visited.put(outEnd,null);
					}
				}
			}
		}
		
		return find(nextLayer,visited,level+1,target,minLength,maxLength,filter);
	}
}
