/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung;

import java.util.List;
import java.util.Map;

import nz.ac.massey.cs.gql4jung.constraints.Term;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.utils.UserDataContainer;

public interface PropertyConstraint<T extends UserDataContainer> extends Constraint {

	public abstract Term[] getTerms();
	public abstract String getOwner();
	// check method suitable if property is attached to one vertex 
	// (references only one role) or one edge
	public abstract boolean check(Graph g, T edgeOrVertex);
	// check method suitable constraint associates properties from
	// different elements (usually vertices)
	public abstract boolean check(Graph g, Map<String,T> bindings);
	// return the owner role id the constraint is attached to a role,
	// or a collection of roles if terms are attached to different constraints
	public abstract List<String> getOwnerRoles();

}