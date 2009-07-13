/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.jmpl;

import nz.ac.massey.cs.gql4jung.Constraint;
/**
 * Instruction used by the constraint scheduler to tell the engine that it must
 * iterate over vertices to bind a certain role.
 * @author jens dietrich
 */
public class LoopInstruction implements Constraint {
	@Override
	public String toString() {
		return "Loop instruction for role: "+role;
	}

	private String role = null;

	public LoopInstruction(String role) {
		super();
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
