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
import java.util.List;
import java.util.Map;

import nz.ac.massey.cs.gql4jung.PropertyConstraint;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.utils.UserDataContainer;

/**
 * Simple key value conditions. Given a vertex v, and a property "type=package",
 * the method should check whether v.getUserDatum("type").equals("package").
 * Note that the following is not yet supported: comparison operators other than
 * equals, data types other than strings.
 * 
 * @author jens.dietrich@gmail.com
 */
public class SimplePropertyConstraint<T extends UserDataContainer> implements
		PropertyConstraint<T> {

	private Term[] terms = null;
	private String owner = null;
	private List<String> ownerSingleton = new ArrayList<String>();

	// the default operator is
	private Operator operator = Operator.getInstance("=");
	
	public void setTerms(Term... terms) {
		this.terms = terms;
	}
	public Term[] getTerms() {
		return terms;
	}
	

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nz.ac.massey.cs.gpl4jung.PropertyConstraint#check(edu.uci.ics.jung.graph
	 * .Graph, T)
	 */
	public boolean check(Graph g, T edgeOrVertex) {
		
		// instantiate
		Object[] values = new Object[terms.length];
		for (int i=0;i<terms.length;i++) {
			if (terms[i] instanceof PropertyTerm) {
				Object value = ((PropertyTerm)terms[i]).getValue(edgeOrVertex);
				values[i] = value;
			}
			else if (terms[i] instanceof ValueTerm) {
				values[i] = ((ValueTerm)terms[i]).getValue();
			}
		}
		return operator.compare(values[0], values[1]);
	}
	
	public boolean check(Graph g, Map<String,T> bindings) {
		
		// instantiate
		Object[] values = new Object[terms.length];
		for (int i=0;i<terms.length;i++) {
			if (terms[i] instanceof RolePropertyTerm) {
				T owner = bindings.get(((RolePropertyTerm)terms[i]).getRole());
				Object value = ((RolePropertyTerm)terms[i]).getValue(owner);
				values[i] = value;
			}
			else if (terms[i] instanceof ValueTerm) {
				values[i] = ((ValueTerm)terms[i]).getValue();
			}
		}
		return operator.compare(values[0], values[1]);
	}
	
	@Override
	public String getOwner() {
		return owner;	
	}
	public void setOwner(String owner){
		this.owner = owner;
		this.ownerSingleton.clear();
		this.ownerSingleton.add(owner);
	}
	public SimplePropertyConstraint clone(){
		SimplePropertyConstraint clone = new SimplePropertyConstraint();
		clone.setTerms(getTerms().clone());
		clone.setOwner(getOwner());
		clone.setOperator(getOperator());
		return clone;
	}
	
	public String toString() {
		if (terms.length==2) {
			return new StringBuffer()
				.append(terms[0])
				.append(operator)
				.append(terms[1])
				.toString();
		}
		else return super.toString();
	}
	
	// return the owner role id the constraint is attached to a role,
	// or a collection of roles if terms are attached to different constraints
	public List<String> getOwnerRoles() {
		if (this.owner!=null) {
			return this.ownerSingleton;
		}
		else {
			List<String> l = new ArrayList<String>(terms.length);
			for (Term t:terms) {
				if (t instanceof RolePropertyTerm) {
					l.add(((RolePropertyTerm)t).getRole());
				}
				else if (!(t instanceof ValueTerm)) {
					throw new IllegalStateException("Property constraints without owner should only have value and role property terms");
				}
			}
			return l;
		}
	}
}
