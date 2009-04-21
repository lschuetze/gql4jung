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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import nz.ac.massey.cs.gql4jung.ConnectedVertex;
import nz.ac.massey.cs.gql4jung.LinkConstraint;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.impl.PathImpl;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPathUtils;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
/**
 * Constraint to check the existence of paths between nodes.
 * @author jens.dietrich@gmail.com
 *
 */
public class GroupConstraint extends LinkConstraint<Path> {
	private String key = null;
	
	public GroupConstraint() {
		super();
		UID pathID = new UID();
		super.id = pathID.toString();
	}
	// TODO straighten generic types
	public Iterator<ConnectedVertex<Path>> getPossibleSources(final Graph g,final Vertex target) {
		
		Iterator<Vertex> iter = g.getVertices().iterator();
		final ShortestPath SPA = new DijkstraShortestPath(g);
		final Map<Vertex,ConnectedVertex<Path>> links = new HashMap<Vertex,ConnectedVertex<Path>>();
		Predicate<Vertex> filter = new Predicate<Vertex>() {
			@Override
			public boolean apply(Vertex v) {
				if (target==v) {
					return false;
				}
				else if(target.getUserDatum(key).equals(v.getUserDatum(key))){
					List path = ShortestPathUtils.getPath(SPA,v,target);
					PathImpl pp = new PathImpl();
					pp.setEdges(path);
					ConnectedVertex<Path> p = new ConnectedVertex(pp,v); 
					links.put(v,p);
					return true;
				}
				return false;
			}
		};
		// vertex to path transformer
		Function<Vertex,ConnectedVertex<Path>> transformer = new Function<Vertex,ConnectedVertex<Path>>() {
			@Override
			public ConnectedVertex<Path> apply(Vertex n) {
				ConnectedVertex<Path> p = links.get(n);
				links.remove(p); //this should make it faster by keeping the size of the cache small
				return p;
			}
		};
		Iterator<Vertex> sources = Iterators.filter(iter,filter);
		return Iterators.transform(sources,transformer);
	}
	public Iterator<ConnectedVertex<Path>>  getPossibleTargets(final Graph g, final Vertex source){
		return this.getPossibleSources(g,source);
		
	}

	public Path check(final Graph g, final Vertex source, final Vertex target){
		PathImpl pp = null;
		if(source.getUserDatum(key).equals(target.getUserDatum(key))){
			final ShortestPath SPA = new DijkstraShortestPath(g);
			List path = ShortestPathUtils.getPath(SPA, source,target);
			pp = new PathImpl();
			pp.setEdges(path);
			pp.setStart(source);
			pp.setEnd(target);
			return pp;
		} else
			return pp;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}



}
