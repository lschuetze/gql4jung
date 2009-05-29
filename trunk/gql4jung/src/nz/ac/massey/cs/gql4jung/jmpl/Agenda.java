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

import java.util.List;

import nz.ac.massey.cs.gql4jung.Constraint;
/**
 * Agenda for resolving constraints.
 * @author jens dietrich
 */
class Agenda {
	private List<Constraint> constraints = null;
	private int cursor = 0;
	
	public Agenda(List<Constraint> constraints) {
		super();
		this.constraints = constraints;
	}
	boolean isDone() {
		return cursor==constraints.size();
	}
	Constraint next() {
		Constraint c = constraints.get(cursor);
		cursor=cursor+1;
		return c;
	}
	void backtrack() {
		cursor=cursor-1;
	}
	
}
