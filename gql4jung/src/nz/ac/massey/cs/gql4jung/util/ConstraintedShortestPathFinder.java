package nz.ac.massey.cs.gql4jung.util;

import java.util.*;
import nz.ac.massey.cs.gql4jung.*;
import com.google.common.base.Predicate;

/**
 * Utility to find the shortest path satisfying certain constraints (length and predicates on edges)
 * @author jens dietrich
 */
public class ConstraintedShortestPathFinder  {
	
	public static Path findLink(Vertex start, Vertex target, int minLength, int maxLength, Predicate<Edge> filter) {
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
