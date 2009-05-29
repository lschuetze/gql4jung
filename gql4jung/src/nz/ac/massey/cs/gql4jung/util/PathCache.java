package nz.ac.massey.cs.gql4jung.util;

import java.util.List;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;
/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


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
		public List<Path> get(DirectedGraph<Vertex, Edge> g, Vertex v,int minLength, int maxLength, boolean outgoing,Predicate<Edge> filter) {
			return null;
		}
		@Override
		public void put(DirectedGraph<Vertex, Edge> g, Vertex v, int minLength,int maxLength, boolean outgoing, Predicate<Edge> filter,List<Path> paths) {
			
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
	public abstract List<Path> get(DirectedGraph<Vertex,Edge> g,Vertex v,int minLength, int maxLength, boolean outgoing, Predicate<Edge> filter);
	/**
	 * Set a list of paths.
	 * @param v
	 * @param minLength
	 * @param maxLength
	 * @param outgoing
	 * @param filter
	 * @return
	 */
	public abstract void put(DirectedGraph<Vertex,Edge> g,Vertex v,int minLength, int maxLength, boolean outgoing, Predicate<Edge> filter,List<Path> path);
	
	/**
	 *  Install this as default instance.
	 */
	public void install() {
		INSTANCE = this;
	}
}
