/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import edu.uci.ics.jung.graph.Graph;
import nz.ac.massey.cs.gql4jung.Constraint;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.PathConstraint;
import nz.ac.massey.cs.gql4jung.PropertyConstraint;

/**
 * Scheduler that keeps the constraints as they are read from the file.
 * @author jens dietrich
 */

public class NullScheduler extends Logging implements ConstraintScheduler {

	@Override
	public List<Constraint> getConstraints(Graph graph,Motif motif) {
		String initialRole = motif.getRoles().get(0);
		LOG_SCHED.debug("Assert initial binding is " + initialRole);
		List<Constraint> oldList = new ArrayList<Constraint>();
		oldList.addAll(motif.getConstraints());
		List<Constraint> newList = new ArrayList<Constraint>(oldList.size());
		Collection<String> boundRoles = new HashSet<String>();
		boundRoles.add(initialRole);

		while (!oldList.isEmpty()) {
			scheduleNext(oldList,newList,boundRoles);
		}
		return newList;
	}

	private void scheduleNext(List<Constraint> oldList,List<Constraint> newList,Collection<String> boundRoles) {
		Constraint c = oldList.get(0);
		if (c instanceof PropertyConstraint) {
			List<String> unboundRoles = getUnboundRoles((PropertyConstraint)c,boundRoles);
			for (String role:unboundRoles) {
				newList.add(new LoopInstruction(role));
				LOG_SCHED.debug("Inserting loop for " + role);
				boundRoles.add(role);
			}
		}
		if (c instanceof PathConstraint) {
			String unboundRole = getUnboundRole((PathConstraint)c,boundRoles);
			if (unboundRole!=null) {
				boundRoles.add(unboundRole);
			}
		}
		newList.add(c);
		oldList.remove(0);
		LOG_SCHED.debug("Scheduling " + c);
	}

	private String getUnboundRole(PathConstraint c,	Collection<String> boundRoles) {
		boolean targetIsBound = boundRoles.contains(c.getTarget());
		boolean sourceIsBound = boundRoles.contains(c.getSource());
		if (targetIsBound && sourceIsBound) return null;
		else if (targetIsBound && !sourceIsBound) return c.getSource();
		else if (!targetIsBound && sourceIsBound) return c.getTarget();
		else {
			throw new IllegalArgumentException("Cannot schedule constraints, a path constraint can only be resolved if source are target are known, this is not the case for " + c + 
			" - move the constraint down or use another constraint scheduler");
		}
	}



	private List<String> getUnboundRoles(PropertyConstraint c,Collection<String> boundRoles) {
		List<String> roles = new ArrayList<String>();
		for (String r:c.getRoles()) {
			if (!boundRoles.contains(r)) {
				roles.add(r);
			}
		}
		return roles;
	}	

}
