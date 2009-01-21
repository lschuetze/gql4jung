/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gpl4jung.constraints;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import nz.ac.massey.cs.gpl4jung.Path;
import nz.ac.massey.cs.gpl4jung.impl.PathImpl;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPathUtils;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.utils.GraphProperties;
/**
 * Constraint to check the existence of paths between nodes.
 * @author jens.dietrich@gmail.com
 *
 */
public class PathConstraint extends LinkConstraint<Path> {
	private int minLength = 1;
	private int maxLength = -1; // this means unbound	
	
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
	
	public Iterator<ConnectedVertex<Path>> getPossibleSources(final Graph g,final Vertex target) {
		
		final Collection<Vertex> nodes= g.getVertices();
		final Iterator<Vertex> vItr = nodes.iterator();
		final ShortestPath SPA = new DijkstraShortestPath(g);
		final Map<Vertex,ConnectedVertex<Path>> links = new HashMap<Vertex,ConnectedVertex<Path>>();
		Predicate filter = new Predicate() {
			@Override
			public boolean evaluate(Object e) {
				Vertex otherNode = (Vertex)e;
				List path = ShortestPathUtils.getPath(SPA,otherNode,target);
				if (path!=null) {
					PathImpl pp = new PathImpl();
					pp.setEdges(path);
					ConnectedVertex<Path> p = new ConnectedVertex(pp,otherNode); // TODO
					links.put(otherNode,p);
				}
				return path!=null;
			}
		};
		// vertex to path transformer
		Transformer transformer = new Transformer() {
			@Override
			public Object transform(Object v) {
				Vertex n = (Vertex)v;
				ConnectedVertex<Path> p = links.get(n);
				links.remove(p); // TODO - this should make it faster by keeping the size of the cache small
				return p;
			}
		};
		Iterator<Vertex>  sources = IteratorUtils.filteredIterator(vItr,filter);
		return IteratorUtils.transformedIterator(sources,transformer);
	}
	public Iterator<ConnectedVertex<Path>>  getPossibleTargets(final Graph g, final Vertex source){
		final Collection<Vertex> nodes= g.getVertices();
		final Iterator<Vertex> vItr = nodes.iterator();
		final ShortestPath SPA = new DijkstraShortestPath(g);
		final Map<Vertex,ConnectedVertex<Path>> links = new HashMap<Vertex,ConnectedVertex<Path>>();
		Predicate filter = new Predicate() {
			@Override
			public boolean evaluate(Object e) {
				Vertex otherNode = (Vertex)e;
				List path = ShortestPathUtils.getPath(SPA, source,otherNode);
				if (path!=null) {
					PathImpl pp = new PathImpl();
					pp.setEdges(path);
					ConnectedVertex<Path> p = new ConnectedVertex(pp,otherNode); // TODO
					links.put(otherNode,p);
				}
				return path!=null;
			}
		};
		// vertex to path transformer
		Transformer transformer = new Transformer() {
			@Override
			public Object transform(Object v) {
				Vertex n = (Vertex)v;
				ConnectedVertex<Path> p = links.get(n);
				links.remove(p); // TODO - this should make it faster by keeping the size of the cache small
				return p;
			}
		};
		Iterator<Vertex>  targets = IteratorUtils.filteredIterator(vItr,filter);
		return IteratorUtils.transformedIterator(targets,transformer);
	}
	public Path check(final Graph g, final Vertex source, final Vertex target){
		final Collection<Vertex> nodes= g.getVertices();
		final ShortestPath SPA = new DijkstraShortestPath(g);
		final Map<Vertex,ConnectedVertex<Path>> links = new HashMap<Vertex,ConnectedVertex<Path>>();
		List path = ShortestPathUtils.getPath(SPA, source,target);
		PathImpl pp = null;
		if (path!=null) {
			pp = new PathImpl();
			pp.setEdges(path);
			ConnectedVertex<Path> p = new ConnectedVertex(pp,source); // TODO
			links.put(source,p);
		};	
		return pp;
	}
}

