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
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

public interface BinaryConstraint<T> extends Constraint {

	/**
	 * Given the target of the link returns possible pairs consisting of a source vertex and a link.
	 * @param g
	 * @param source
	 * @return
	 */
	Iterator<ConnectedVertex<T>> getPossibleSources(Graph g,Vertex source);
	/**
	 * Given the source of the link returns possible pairs consisting of a target vertex and a link.
	 * @param g
	 * @param source
	 * @return
	 */
	Iterator<ConnectedVertex<T>>  getPossibleTargets(Graph g,Vertex source);
	/**
	 * returns a link (edge or path) if one exists, null otherwise.
	 * @param g
	 * @param source
	 * @param target
	 * @return
	 */
	T  check(Graph g,Vertex source,Vertex target);
}
