package nz.ac.massey.cs.gql4jung.util;

import java.util.*;

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;

import com.google.common.base.Predicate;

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Utility to find paths in graphs.
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
	public static Iterator<Path> findLinks(DirectedGraph<Vertex,Edge> g,Vertex start, int minLength, int maxLength, boolean outgoing, Predicate<Edge> filter) {
		// try cache first
		List<Path> coll = PathCache.INSTANCE.get(g, start, minLength, maxLength, outgoing, filter);
		if (coll!=null) return coll.iterator();
		
		// we pre init the iterator, this should be done on demand
		coll = new ArrayList<Path> ();
		Map<Vertex,Object> visited = new IdentityHashMap<Vertex,Object>();
		List<Path> layer = new ArrayList<Path>();
		
		Path initialPath = new Path(start);
		layer.add(initialPath);
		if (minLength==0) {
			coll.add(initialPath);
			visited.put(start,null);
		}
		
		collectPaths(1,coll,visited,layer,start,minLength,maxLength,outgoing,filter);
		// cache
		PathCache.INSTANCE.put(g,start, minLength, maxLength, outgoing, filter, coll);
		
		return coll.iterator();
		
	}
	private static void collectPaths(int i, Collection<Path> coll,Map<Vertex, Object> visited, Collection<Path> lastLayer,Vertex start, int minLength, int maxLength, boolean outgoing, Predicate<Edge> filter) {
		if (maxLength!=-1 && i>maxLength) return ; // do not continue
		Collection<Path> nextLayer = new ArrayList<Path>();
		boolean hasNewVertices = false;
		for (Path edges:lastLayer) {
			Vertex end = getEnd(edges,outgoing);
			Collection<Edge> edges1 = getNext(end,outgoing);
			for (Edge edge:edges1) {
				if (filter==null || filter.apply(edge)) {
					Vertex newEnd = getEndPoint(edge,outgoing);
					if (!visited.containsKey(newEnd)) {
						// new node, not yet seen
						hasNewVertices = true;
						visited.put(newEnd, null);
						// if min length is ok, add this
						Path newPath = outgoing?edges.addAtEnd(edge):edges.addAtStart(edge);
						nextLayer.add(newPath);
						if (i>=minLength) {
							coll.add(newPath);
						}
					}
				}
			}
		}
		if (hasNewVertices) {
			collectPaths(i+1, coll, visited, nextLayer,start,minLength,maxLength,outgoing,filter);
		}

	}
	private static Vertex getEndPoint(Edge edge,boolean outgoing) {
		if (outgoing) return (Vertex) edge.getEnd();
		else return (Vertex) edge.getStart();
	}
	private static Collection<Edge> getNext(Vertex v,boolean outgoing) {
		return outgoing?v.getOutEdges():v.getInEdges();
	}
	private static Vertex getEnd(Path path,boolean outgoing) {
		if (outgoing) return path.getEnd();
		else return path.getStart();
	}

}
