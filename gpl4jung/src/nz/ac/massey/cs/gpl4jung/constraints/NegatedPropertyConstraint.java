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

import nz.ac.massey.cs.gpl4jung.PropertyConstraint;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.utils.UserDataContainer;

public class NegatedPropertyConstraint<T extends UserDataContainer> implements PropertyConstraint<T> {
	private PropertyConstraint<T> part = null;
	public boolean check(Graph g, T... edgeOrVertex) {
		return !part.check(g,edgeOrVertex);
	}
	public PropertyConstraint<T> getPart() {
		return part;
	}
	public void setPart(PropertyConstraint<T> part) {
		this.part = part;
	}

}