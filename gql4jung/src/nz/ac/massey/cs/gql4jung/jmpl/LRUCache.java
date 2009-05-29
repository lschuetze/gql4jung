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

import java.util.List;
import org.apache.commons.collections15.map.LRUMap;
import com.google.common.base.Predicate;
import edu.uci.ics.jung.graph.DirectedGraph;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.util.PathCache;
/**
 * Cache based on a LRU map.
 * Limitations: 
 * <ol>
 * <li>Works only for one graph at a time, not suitable if different graphs are processed in parallel</li>
 * <li>Assumes filters are stateless (predicates usually are)</li>
 * </ol>
 * @author jens dietrich
 */
public class LRUCache extends PathCache {
	private DirectedGraph<Vertex,Edge> graph = null;
	private LRUMap<Key,List<Path>> cache = null;
	private int reusecounter = 0;
	private Key querykey = new Key();
	private int size = 1000;
	public LRUCache(DirectedGraph<Vertex, Edge> graph, int size) {
		super();
		this.graph = graph;
		this.size = size;
		this.cache = new LRUMap<Key,List<Path>>(size);
	}

	private class Key {
		Vertex v = null;
		boolean outgoing = true;		
		int minLength = 1;
		int maxLength = -1;
		Predicate<Edge> filter = null;
		
		public Key(Vertex v, int maxLength, int minLength,boolean outgoing,Predicate<Edge> filter ) {
			super();
			this.filter = filter;
			this.maxLength = maxLength;
			this.minLength = minLength;
			this.outgoing = outgoing;
			this.v = v;
		}
		public Key() {
			super();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			//result = prime * result + getOuterType().hashCode();
			result = prime * result + ((filter == null) ? 0 : filter.hashCode());
			result = prime * result + maxLength;
			result = prime * result + minLength;
			result = prime * result + (outgoing ? 1231 : 1237);
			result = prime * result + ((v == null) ? 0 : v.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (filter == null) {
				if (other.filter != null)
					return false;
			} else if (!filter.equals(other.filter))
				return false;
			if (maxLength != other.maxLength)
				return false;
			if (minLength != other.minLength)
				return false;
			if (outgoing != other.outgoing)
				return false;
			if (v == null) {
				if (other.v != null)
					return false;
			} else if (!v.equals(other.v))
				return false;
			return true;
		}

		private LRUCache getOuterType() {
			return LRUCache.this;
		}
	}

	@Override
	public synchronized List<Path> get(DirectedGraph<Vertex, Edge> g, Vertex v,int minLength, int maxLength, boolean outgoing,Predicate<Edge> filter) {
		if (graph!=g) {
			badRequest(g);
			return null;
		}
		// reuse the same key
		this.querykey.v=v;
		this.querykey.minLength=minLength;
		this.querykey.maxLength=maxLength;
		this.querykey.outgoing=outgoing;
		this.querykey.filter=filter;
		
		return cache.get(querykey);
		
		// code used to keep tack of reuse levels
		/*
		List<Path> cached = cache.get(querykey); 
		if (cache!=null) {
			this.reusecounter = this.reusecounter+1;
			if (this.reusecounter%100000==0) {
				System.out.println("reuse counter is "+this.reusecounter);
			}
		}
		return cached;
		*/
		
	}

	@Override
	public synchronized void put(DirectedGraph<Vertex, Edge> g, Vertex v, int minLength,	int maxLength, boolean outgoing, Predicate<Edge> filter,List<Path> paths) {
		if (graph!=g) {
			badRequest(g);
		}
		// only cache if long paths are in here
		/*
		else if (paths.size()==0) {
			return;
		}
		else if (paths.get(paths.size()-1).size()<2) {
			return;
		}
		*/
		else {
			//if (cache.isFull()) System.out.println("cache is full");
			Key key = new Key(v,minLength,maxLength,outgoing,filter);
			cache.put(key,paths);
			//if (cache.size()%100==0) System.out.println("cache size is "+cache.size());
		}
	}

	private void badRequest(DirectedGraph<Vertex,Edge> g) {
		Logging.LOG_CACHE.warn("Cache request for graph " + g + " - will be ignored by cache set up for graph " + this.graph);
	}
}
