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

/**
 * Simple key value conditions. 
 * Given a vertex v, and a property "type=package", the method should check 
 * whether v.getUserDatum("type").equals("package").
 * Note that the following is not yet supported: comparison operators other than equals, data types other than strings. 
 * @author jens.dietrich@gmail.com
 */
public class SimplePropertyConstraint<T extends UserDataContainer> implements PropertyConstraint<T> {

	private String key = null;
	private Object value = null;
	// operators are string defined in http://www.w3.org/TR/xpath-functions/#regex-syntax
	// the default operator is 
	private Operators operator = Operators.EQUALS;
	
	public Operators getOperator() {
		return operator;
	}

	public void setOperator(Operators operator) {
		this.operator = operator;
	}

	/* (non-Javadoc)
	 * @see nz.ac.massey.cs.gpl4jung.PropertyConstraint#check(edu.uci.ics.jung.graph.Graph, T)
	 */
	public boolean check(Graph g,T edgeOrVertex) {
		// TODO
		return false;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
