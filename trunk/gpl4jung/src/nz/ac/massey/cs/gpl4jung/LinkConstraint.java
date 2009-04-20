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
 * Constraint describing that vertexes are connected (by an edge or a path).
 * @author jens.dietrich@gmail.com
 *
 * @param <T>
 */
public abstract class LinkConstraint<T> implements Constraint {

	private PropertyConstraint<Edge> edgePropertyConstraint = null;
	private String predicate = null, source=null, target=null;
	protected String id = null;
	public void setPredicate(String predicate){
		this.predicate = predicate;
	}
	public String getPredicate(){
		return predicate;
	}
	public String getID(){
		return id;
	}
	/**
	 * Given the target of the link returns possible pairs consisting of a source vertex and a link.
	 * @param g
	 * @param target
	 * @return
	 */
	public abstract Iterator<ConnectedVertex<T>> getPossibleSources(Graph g,Vertex target);
	/**
	 * Given the source of the link returns possible pairs consisting of a target vertex and a link.
	 * @param g
	 * @param source
	 * @return
	 */
	public abstract Iterator<ConnectedVertex<T>>  getPossibleTargets(Graph g,Vertex source);
	/**
	 * returns a link (edge or path) if one exists, null otherwise.
	 * @param g
	 * @param source
	 * @param target
	 * @return
	 */
	public abstract T  check(Graph g,Vertex source,Vertex target);
	/**
	 * Get the property constraints for the link. If there are more than one,
	 * use conjunction.
	 * @return
	 */
	public PropertyConstraint<Edge> getEdgePropertyConstraint() {
		return edgePropertyConstraint;		
	}
	public void setEdgePropertyConstraint(PropertyConstraint<Edge> edgePropertyConstraint) {
		this.edgePropertyConstraint = edgePropertyConstraint;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	};
}
