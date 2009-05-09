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

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.iterators.SingletonIterator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import nz.ac.massey.cs.gql4jung.ConnectedVertex;
import nz.ac.massey.cs.gql4jung.LinkConstraint;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.impl.EmptyPath;
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
public class PathConstraint extends LinkConstraint<Path> {
	private int maxLength = -1; // this means unbound	
	private int minLength = -1;
	//private String id = null;
	
	public PathConstraint() {
		super();
		UID pathID = new UID();
		super.id = pathID.toString();
	}
	
	public Iterator<ConnectedVertex<Path>> getPossibleSources(final Graph g,final Vertex target) {
		
		final Collection<Vertex> nodes= g.getVertices();
		final Iterator<Vertex> vItr = nodes.iterator();
		final ShortestPath SPA = new DijkstraShortestPath(g);
		final Map<Vertex,ConnectedVertex<Path>> links = new HashMap<Vertex,ConnectedVertex<Path>>();
		Predicate<Vertex> filter = new Predicate<Vertex>() {
			@Override
			public boolean apply(Vertex v) {
				List path = ShortestPathUtils.getPath(SPA,v,target);
				if (checkPath(path)) {
					PathImpl pp = new PathImpl();
					pp.setEdges(path);
					ConnectedVertex<Path> p = new ConnectedVertex(pp,v); 
					links.put(v,p);
				}
				return path.size()!=0;
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
		Iterator<Vertex> sources = Iterators.filter(vItr,filter);
		return Iterators.transform(sources,transformer);
	}
	public Iterator<ConnectedVertex<Path>>  getPossibleTargets(final Graph g, final Vertex source){
		final Collection<Vertex> nodes= g.getVertices();
		final Iterator<Vertex> nItr = nodes.iterator();
		
		final ShortestPath SPA = new DijkstraShortestPath(g);
		final Map<Vertex,ConnectedVertex<Path>> links = new HashMap<Vertex,ConnectedVertex<Path>>();
		Predicate<Vertex> filter = new Predicate<Vertex>() {
			@Override
			public boolean apply(Vertex v) {
				Vertex otherNode = (Vertex)v;
				if(v==source){
					EmptyPath emptyPath = new EmptyPath(otherNode);
					emptyPath.setVertex(otherNode);
					ConnectedVertex<Path> p = new ConnectedVertex(emptyPath,otherNode);
					links.put(otherNode, p);
					return true;
				}
				List path = ShortestPathUtils.getPath(SPA, source,otherNode);
				if (checkPath(path)) {
					PathImpl pp = new PathImpl();
					pp.setEdges(path);
					ConnectedVertex<Path> p = new ConnectedVertex(pp,otherNode); 
					links.put(otherNode,p);
				}
				return path.size()!=0;
			}
		};
		// vertex to path transformer
		Function<Vertex,ConnectedVertex<Path>> transformer = new Function<Vertex,ConnectedVertex<Path>>() {

			@Override
			public ConnectedVertex<Path> apply(Vertex n) {
					ConnectedVertex<Path> p = links.get(n);
					links.remove(p); // this should make it faster by keeping the size of the cache small
					return p;
				}			
		};
		if(minLength==0){
			Iterator<Vertex> vItr=null;
			Iterator<Vertex> thisI = new SingletonIterator(source);
			vItr = IteratorUtils.chainedIterator(thisI, nItr);
			Iterator<Vertex>  targets = Iterators.filter(vItr,filter);
			return Iterators.transform(targets,transformer);
		}
		Iterator<Vertex>  targets = Iterators.filter(nItr,filter);
		return Iterators.transform(targets,transformer);
	}
	private boolean checkPath(List path) {
		return path.size()!=0 && (maxLength==-1 || path.size()<maxLength);
	}
	public Path check(final Graph g, final Vertex source, final Vertex target){
		final ShortestPath SPA = new DijkstraShortestPath(g);
		List path = ShortestPathUtils.getPath(SPA, source,target);
		PathImpl pp = null;
		if (checkPath(path)) {
			pp = new PathImpl();
			pp.setEdges(path);
			pp.setStart(source);
			pp.setEnd(target);
		};	
		return pp;
	}

	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}


}

