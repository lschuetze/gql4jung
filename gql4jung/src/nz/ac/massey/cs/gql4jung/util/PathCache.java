package nz.ac.massey.cs.gql4jung.util;

import java.util.Collection;

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;

import com.google.common.base.Predicate;

import edu.uci.ics.jung.graph.DirectedGraph;
/**
 * Simple structure  used to cache paths data. 
 * @author jens dietrich
 */
public abstract class PathCache {
	// using the default means no caching is used
	
	private static PathCache NO_CACHE = new PathCache() {
		@Override
		public Collection<Path> get(DirectedGraph<Vertex, Edge> g, Vertex v,int minLength, int maxLength, boolean outgoing,Predicate<Edge> filter) {
			return null;
		}
		@Override
		public void put(DirectedGraph<Vertex, Edge> g, Vertex v, int minLength,int maxLength, boolean outgoing, Predicate<Edge> filter,Collection<Path> path) {
			
		}
	};
	public static PathCache INSTANCE = NO_CACHE;
	// switch caching off by setting the instance to NO_CACHE
	public static void switchCachingOff() {
		INSTANCE = NO_CACHE;
	}
	/**
	 * Get a list of paths or null if there is no entry in the cache.
	 * @param v
	 * @param minLength
	 * @param maxLength
	 * @param outgoing
	 * @param filter
	 * @return
	 */
	public abstract Collection<Path> get(DirectedGraph<Vertex,Edge> g,Vertex v,int minLength, int maxLength, boolean outgoing, Predicate<Edge> filter);
	/**
	 * Set a list of paths.
	 * @param v
	 * @param minLength
	 * @param maxLength
	 * @param outgoing
	 * @param filter
	 * @return
	 */
	public abstract void put(DirectedGraph<Vertex,Edge> g,Vertex v,int minLength, int maxLength, boolean outgoing, Predicate<Edge> filter,Collection<Path> path);
}
