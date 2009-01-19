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
	
private String source=null ,target =null;
	
	public String getSource(){
		return source;
	}
	public void setSource(String source){
		this.source = source;
	}
	public String getTarget(){
		return target;
	}
	public void setTarget(String target){
		this.target = target;
	}

	public Iterator<ConnectedVertex<Edge>> getPossibleSources(final Graph g,final Vertex target) {
		Iterator<Edge> incomingEdges = target.getInEdges().iterator();
		
		Transformer transformer = new Transformer() {
			@Override
			public Object transform(Object v) {
				Edge e = (Edge)v;
				return e.getEndpoints().getFirst();
			}
		};		
		return IteratorUtils.transformedIterator(incomingEdges,transformer);
	}
	public Iterator<ConnectedVertex<Edge>> getPossibleTargets(final Graph g,final Vertex source){
	Iterator<Edge> outgoingEdges = source.getOutEdges().iterator();
		
		Transformer transformer = new Transformer() {
			@Override
			public Object transform(Object v) {
				Edge e = (Edge)v;
				return e.getEndpoints().getFirst();
			}
		};		
		return IteratorUtils.transformedIterator(outgoingEdges,transformer);
	}
	
	public Edge check(final Graph g,final Vertex source, final Vertex target){
		
		Edge edge = source.findEdge(target);
		return edge;
	}
}
