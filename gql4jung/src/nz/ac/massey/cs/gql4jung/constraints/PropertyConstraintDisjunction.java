/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.constraints;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nz.ac.massey.cs.gql4jung.PropertyConstraint;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.utils.UserDataContainer;
/**
 * Complex condition using OR.
 * @author jens.dietrich@gmail.com
 *
 * @param <T>
 */
public class PropertyConstraintDisjunction<T  extends UserDataContainer> extends ComplexPropertyConstraint<T> {
	
	private Term[] terms = null;
	private String owner = null;
	
	public boolean check(Graph g, T edgeOrVertex) {
		for (PropertyConstraint part:parts) {
			if (part.check(g,edgeOrVertex)) {
				return true;
			}
		}
		return false;
	}
	public boolean check(Graph g, Map<String,T> bindings) {
		for (PropertyConstraint part:parts) {
			if (part.check(g,bindings)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner){
		this.owner = owner;
	}

	@Override
	public Term[] getTerms() {
		return terms;
	}
	public void setTerms(Term... terms) {
		this.terms = terms;
	}
	public PropertyConstraintDisjunction clone(){
		PropertyConstraintDisjunction clone = new PropertyConstraintDisjunction();
		List<PropertyConstraint> parts = new ArrayList<PropertyConstraint>();
		for(Iterator itr=this.parts.iterator();itr.hasNext();){
			SimplePropertyConstraint p = (SimplePropertyConstraint) itr.next();
			parts.add(p.clone());
		}
		clone.setParts(parts);	
		clone.setOwner(getOwner());
		clone.setTerms(getTerms());
		return clone;
	}

	public String toString() {
		boolean f = true;
		StringBuffer b = new StringBuffer();
		for (PropertyConstraint c:this.parts) {
			if (f) f=false;
			else b.append(" or ");
			b.append(c);
		}
		return b.toString();
	}
}
