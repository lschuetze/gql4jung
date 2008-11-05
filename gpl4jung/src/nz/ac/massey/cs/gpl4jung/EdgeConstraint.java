/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gpl4jung;

import java.util.Iterator;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
/**
 * Constraint to check the existence of edges connecting nodes.
 * @author jens.dietrich@gmail.com
 *
 */
public class EdgeConstraint implements BinaryConstraint<Edge> {

	public Iterator<ConnectedVertex<Edge>> getPossibleSources(Graph g,Vertex source) {
		return null; // TODO
	}
	public Iterator<ConnectedVertex<Edge>>  getPossibleTargets(Graph g,Vertex source){
		return null; // TODO
	}
	public Edge  check(Graph g,Vertex source,Vertex target){
		return null; // TODO
	}
	
}
