package nz.ac.massey.cs.gql4jung.jmpl;

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

public class SimpleScheduler extends Logging implements ConstraintScheduler {

	@Override
	public List<Constraint> getConstraints(Graph graph,Motif motif) {
		String initialRole = motif.getRoles().get(0);
		LOG_SCHED.debug("Assert initial binding is " + initialRole);
		List<Constraint> oldList = new ArrayList<Constraint>();
		oldList.addAll(motif.getConstraints());
		List<Constraint> newList = new ArrayList<Constraint>(oldList.size());
		Collection<String> boundRoles = new HashSet<String>();
		boundRoles.add(initialRole);

		while (0<oldList.size()) {
			scheduleNext(oldList,newList,boundRoles);
		}
		return newList;
	}

	private void scheduleNext(List<Constraint> oldList,List<Constraint> newList,Collection<String> boundRoles) {
		// try to find property constraint
		for (PropertyConstraint pc:this.getPropertyConstraints(oldList)) {
			if (boundRoles.containsAll((pc.getRoles()))) {
				newList.add(pc);
				oldList.remove(pc);
				LOG_SCHED.debug("Scheduling " + pc);
				return;
			}
		}
		// try to find path a constraint with both ends knows
		for (PathConstraint pc:this.getPathConstraints(oldList)) {
			if (boundRoles.contains(pc.getSource()) && boundRoles.contains(pc.getTarget())) {
				newList.add(pc);
				oldList.remove(pc);
				LOG_SCHED.debug("Scheduling " + pc);
				return;
			}
		}
		
		// try to find path constraint with one end known
		for (PathConstraint pc:this.getPathConstraints(oldList)) {
				if (boundRoles.contains(pc.getSource())) {
					newList.add(pc);
					oldList.remove(pc);
					// add new role
					boundRoles.add(pc.getTarget());
					LOG_SCHED.debug("Scheduling " + pc);
					return;
				}
				else if (boundRoles.contains(pc.getTarget())) {
					newList.add(pc);
					oldList.remove(pc);
					// add new role
					boundRoles.add(pc.getSource());
					LOG_SCHED.debug("Scheduling " + pc);
					return;
				}
		}
		
		// try to find a property with only one open role
		for (PropertyConstraint pc:this.getPropertyConstraints(oldList)) {
			String missingRole = this.getOneMissing(boundRoles,pc.getRoles());
			if (missingRole!=null) {
				newList.add(new LoopInstruction(missingRole));
				boundRoles.add(missingRole);
				LOG_SCHED.debug("Inserting loop for " + missingRole);
				// do not remove constraint, this property constraint will be selected in the next step
				return;
			}
		}

		
		LOG_SCHED.warn("Scheduling did not succeed for all constraints, listing left overs:");
		for (Constraint c:oldList) {
			LOG_SCHED.warn(c);
		}
		
		throw new IllegalStateException("Cannot schedule constraints");
	}
	

	private String getOneMissing(Collection<String> boundRoles, List<String> roles) {
		int c = roles.size();
		String missing = null;
		for (String role:roles) {
			if (boundRoles.contains(role)) {
				c = c-1;
			}
			else {
				missing=role;
			}
		}
		return c==1?missing:null;
	}

	private List<PathConstraint> getPathConstraints(Collection<Constraint> coll) {
		List<PathConstraint> list = new ArrayList<PathConstraint>();
		for (Constraint c:coll) {
			if (c instanceof PathConstraint) {
				list.add((PathConstraint)c);
			}
		}
		return list;
	}
	private List<PropertyConstraint> getPropertyConstraints(Collection<Constraint> coll) {
		List<PropertyConstraint> list = new ArrayList<PropertyConstraint>();
		for (Constraint c:coll) {
			if (c instanceof PropertyConstraint) {
				list.add((PropertyConstraint)c);
			}
		}
		return list;
	}
}
