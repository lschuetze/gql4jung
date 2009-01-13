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
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
/**
 * Constraint to check the existence of edges connecting nodes.
 * @author jens.dietrich@gmail.com
 *
 */
public class EdgeConstraint extends LinkConstraint<Edge> {

	public Iterator<ConnectedVertex<Edge>> getPossibleSources(final Graph g,final Vertex target) {
		final Collection<Vertex> nodes= g.getVertices();
		final DijkstraShortestPath DSP = new DijkstraShortestPath(g);
		final Iterator<Vertex> vItr = nodes.iterator();
		final Map<Vertex,ConnectedVertex<Edge>> links = new HashMap<Vertex,ConnectedVertex<Edge>>();
		Predicate filter = new Predicate() {
			@Override
			public boolean evaluate(Object e) {
				Vertex otherNode = (Vertex)e;
				Edge edge = DSP.getIncomingEdge(otherNode, target);
				if (edge!=null) {
					ConnectedVertex<Edge> e1 = new ConnectedVertex(edge,otherNode); 
					links.put(otherNode,e1);
				}
				return edge!=null;
			}
		};
		// vertex to edge transformer
		Transformer transformer = new Transformer() {
			@Override
			public Object transform(Object v) {
				Vertex n = (Vertex)v;
				ConnectedVertex<Edge> e1 = links.get(n);
				links.remove(e1); // TODO - this should make it faster by keeping the size of the cache small
				return e1;
			}
		};
		Iterator<Vertex>  sources = IteratorUtils.filteredIterator(vItr,filter);
		return IteratorUtils.transformedIterator(sources,transformer);
	}
	public Iterator<ConnectedVertex<Edge>> getPossibleTargets(final Graph g,final Vertex source){
		final Collection<Vertex> nodes= g.getVertices();
		final DijkstraShortestPath DSP = new DijkstraShortestPath(g);
		final Iterator<Vertex> vItr = nodes.iterator();
		final Map<Vertex,ConnectedVertex<Edge>> links = new HashMap<Vertex,ConnectedVertex<Edge>>();
		Predicate filter = new Predicate() {
			@Override
			public boolean evaluate(Object e) {
				Vertex otherNode = (Vertex)e;
				Edge edge = DSP.getIncomingEdge(source, otherNode);
				if (edge!=null) {
					ConnectedVertex<Edge> e1 = new ConnectedVertex(edge,otherNode); 
					links.put(otherNode,e1);
				}
				return edge!=null;
			}
		};
		// vertex to edge transformer
		Transformer transformer = new Transformer() {
			@Override
			public Object transform(Object v) {
				Vertex n = (Vertex)v;
				ConnectedVertex<Edge> e1 = links.get(n);
				links.remove(e1); // TODO - this should make it faster by keeping the size of the cache small
				return e1;
			}
		};
		Iterator<Vertex> targets = IteratorUtils.filteredIterator(vItr,filter);
		return IteratorUtils.transformedIterator(targets,transformer);
	}
	public Edge check(final Graph g,final Vertex source, final Vertex target){
		final DijkstraShortestPath DSP = new DijkstraShortestPath(g);
		Edge edge = DSP.getIncomingEdge(source, target);
		return edge;
	}
}
