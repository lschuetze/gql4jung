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

import java.rmi.server.UID;
import java.util.Iterator;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
/**
 * Constraint to check the existence of edges connecting nodes.
 * @author jens.dietrich@gmail.com
 *
 */
public class EdgeConstraint extends LinkConstraint<Edge> {
	
private String id = null;
	
	public EdgeConstraint() {
		super();
		UID edgeID = new UID();
		this.id = edgeID.toString();
	}
	
	public String getEdgeID(){
		return id;
	}
	
	public Iterator<ConnectedVertex<Edge>> getPossibleSources(final Graph g,final Vertex target) {
		Iterator<Edge> incomingEdges = target.getInEdges().iterator();
		
		Function<Edge,ConnectedVertex<Edge>> transformer = new Function<Edge,ConnectedVertex<Edge>>() {
			@Override
			public ConnectedVertex<Edge> apply(Edge e) {
				Vertex v = (Vertex) e.getEndpoints().getFirst();
				ConnectedVertex<Edge> ve = new ConnectedVertex<Edge> (e,v);
				return ve;
			}
		};		
		return Iterators.transform(incomingEdges,transformer);
	}
	public Iterator<ConnectedVertex<Edge>> getPossibleTargets(final Graph g,final Vertex source){
		Iterator<Edge> outgoingEdges = source.getOutEdges().iterator();
		
		Function<Edge,ConnectedVertex<Edge>> transformer = new Function<Edge,ConnectedVertex<Edge>>(){

			@Override
			public ConnectedVertex<Edge> apply(Edge e) {
				Vertex v = (Vertex) e.getEndpoints().getSecond();
				ConnectedVertex<Edge> ve = new ConnectedVertex<Edge>(e,v);
				return ve;
			}
		};		
		return Iterators.transform(outgoingEdges, transformer);
	}
	
	public Edge check(final Graph g,final Vertex source, final Vertex target){
		
		Edge edge = source.findEdge(target);
		return edge;
	}
}