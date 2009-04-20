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

import edu.uci.ics.jung.utils.UserDataContainer;
/**
 * Property term. For instance, when a vertex with the id (role name) "class1" is used, 
 * then a property term might refer to the "isAbstract" property. 
 * "isAbstract" would be the key.
 * @author jens.dietrich@gmail.com
 */

public class PropertyTerm  implements Term {
	public PropertyTerm(String key) {
		super();
		this.key = key;
	}


	private String key = null;  // this is the property key that can be used to query the property

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	
	public Object getValue(UserDataContainer vertexOrEdge) {
		return vertexOrEdge.getUserDatum(key);
	}
	public PropertyTerm clone(){
		return new PropertyTerm(getKey());
	}
}
