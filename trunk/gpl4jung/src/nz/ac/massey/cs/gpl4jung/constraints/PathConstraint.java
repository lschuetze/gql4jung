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

import java.util.Iterator;

import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import nz.ac.massey.cs.gpl4jung.Path;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
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
	
	public Iterator<ConnectedVertex<Path>> getPossibleSources(Graph g,Vertex target) {
		return null; // TODO
	}
	public Iterator<ConnectedVertex<Path>>  getPossibleTargets(Graph g,Vertex source){
		return null; // TODO
	}
	public Path  check(Graph g,Vertex source,Vertex target){
		return null; // TODO
	}
	
}
