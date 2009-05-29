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
	
	public static Path findLink(DirectedGraph<Vertex,Edge> g,Vertex start, Vertex target, int minLength, int maxLength, Predicate<Edge> filter) {
		Map<Vertex,Object> visited = new IdentityHashMap<Vertex,Object>();
		Collection<Path> layer = new ArrayList<Path>();
		
		Path initialPath = new Path(start);
		if (minLength==0 && start==target) {
			return initialPath;
		}
		
		layer.add(initialPath);		
		return find(layer,visited,1,target,minLength,maxLength,filter);		
	}
	
	
	private static Path find(Collection<Path> lastLayer,Map<Vertex,Object> visited,int level, Vertex target, int minLength, int maxLength, Predicate<Edge> filter) {
		// path is too long, give up
		if (maxLength!=-1 && level>maxLength) return null;
		// nothing more todo
		if (lastLayer.isEmpty()) return null;
		
		Collection<Path> nextLayer = new ArrayList<Path>();
		for (Path path:lastLayer) {
			Vertex end = path.getEnd();
			Collection<Edge> outEdges = end.getOutEdges();
			for (Edge nextEdge:outEdges) {
				if (filter.apply(nextEdge)) {
					Vertex outEnd = (Vertex)nextEdge.getEnd();
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
