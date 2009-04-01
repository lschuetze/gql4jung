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

import java.util.List;

import nz.ac.massey.cs.gpl4jung.PropertyConstraint;
import edu.uci.ics.jung.utils.UserDataContainer;

public abstract class ComplexPropertyConstraint<T extends UserDataContainer> implements PropertyConstraint<T>{
	public ComplexPropertyConstraint() {
		super();
	}
	public ComplexPropertyConstraint(List<PropertyConstraint<T>> parts) {
		super();
		this.parts = parts;
	}

	protected List<PropertyConstraint<T>> parts = null;

	public List<PropertyConstraint<T>> getParts() {
		return parts;
	}

	public void setParts(List<PropertyConstraint<T>> parts) {		
		this.parts = parts;
	}

	public Object clone(){
		return clone();
	}
	
}