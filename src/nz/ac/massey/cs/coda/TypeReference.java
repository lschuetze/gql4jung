/**
 * Copyright 2010 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.coda;

import nz.ac.massey.cs.gql4jung.Edge;

/**
 * Custom edge class.
 * @author jens dietrich
 */

@SuppressWarnings("serial")
public class TypeReference extends Edge<TypeNode>{
	private String type = null;
	
	public TypeReference(String id, TypeNode end, TypeNode start) {
		super(id,end,start);
	}
	public TypeReference() {
		super();
	}
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	
	
	public String toString() {
		return new StringBuffer() 			
			.append(this.getId())
			.append(':')
			.append('[')
			.append(this.getStart())
			.append(" ")
			.append(this.type)
			.append(" ")
			.append(this.getEnd())
			.append(']')
			.toString();
	}
	public void copyValuesTo(TypeReference e) {
		super.copyValuesTo(e);
		e.setType(type);
	}
}
