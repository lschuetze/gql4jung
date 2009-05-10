package nz.ac.massey.cs.gql4jung.jmpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

import nz.ac.massey.cs.gql4jung.Constraint;
import nz.ac.massey.cs.gql4jung.LinkConstraint;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.PropertyConstraint;
import nz.ac.massey.cs.gql4jung.constraints.EdgeConstraint;
import nz.ac.massey.cs.gql4jung.constraints.GroupConstraint;
import nz.ac.massey.cs.gql4jung.constraints.OutGroupConstraint;
import nz.ac.massey.cs.gql4jung.constraints.PathConstraint;
import nz.ac.massey.cs.gql4jung.constraints.PropertyTerm;
import nz.ac.massey.cs.gql4jung.constraints.Term;
import nz.ac.massey.cs.gql4jung.constraints.ValueTerm;

/**
 * Simple constraint scheduler.
 * @author jens dietrich
 */

public class ConstraintSchedulerImpl implements ConstraintScheduler {
	List <String> roles = null;
	@Override
	// TODO - remove
	public List<Constraint> getConstraints(Motif motif) {
		this.roles = motif.getRoles();
		List<Constraint> constraints = new ArrayList<Constraint>();
		for(Iterator itr = motif.getConstraints().iterator();itr.hasNext();){
			Constraint nextConstraint = (Constraint) itr.next();
			constraints.add(nextConstraint);
		}
		return constraints; 
	}

	@Override
	public List<Constraint> prepare(Graph g, List<Constraint> constraints) {
		List<Constraint> interimConstraints = new ArrayList<Constraint>();	
		for(Constraint c:constraints){
			if(c instanceof PropertyConstraint){
				interimConstraints.add(c);
			}
		}
		for(Constraint c:constraints){
			if(c instanceof EdgeConstraint){
				interimConstraints.add(c);
			}
		}
		for(Constraint c:constraints){
			if(c instanceof PathConstraint){
				interimConstraints.add(c);
			}
		}
		for(Constraint c:constraints){
			if(c instanceof GroupConstraint){
				interimConstraints.add(c);
			}
		}
		for(Constraint c:constraints){
			if(c instanceof OutGroupConstraint){
				interimConstraints.add(c);
			}
		}
		//List<Constraint> sortedConstraints = sort(interimConstraints);
		//return sortedConstraints;
		return interimConstraints;
	}

	private boolean hasBinding(PropertyConstraint pc,Bindings b) {
		for (Object role:pc.getOwnerRoles()) {
			if (null== b.lookup((String)role)) return false;
		}
		return true;
	}
	private int getBindingsCount(LinkConstraint lc,Bindings b) {
		int i = null==b.lookup(lc.getTarget())?0:1;
		i = i+(null==b.lookup(lc.getSource())?0:1);
		return i;
	}
	
	@Override
	public Constraint selectNext(Graph g, List<Constraint> agenda, Bindings bindings) {
		// prefer property constraints with bindings
		for(Constraint c:agenda){
			if(c instanceof PropertyConstraint){
				if (this.hasBinding((PropertyConstraint)c,bindings)) {
					return c;
				}
			}
		}
		// link constraints with bindings - edge constraints must be first after prepare
		LinkConstraint link1 = null;
		LinkConstraint link0 = null;
		for(Constraint c:agenda){
			if (c instanceof LinkConstraint){
				LinkConstraint lc = (LinkConstraint)c;
				link0  = lc;
				int i = this.getBindingsCount(lc, bindings);
				if (i==0 && link0==null) link0=lc;
				else if (i==1 && link1==null) link1=lc;
				else if (i==2) return lc;
			}
		}
		if (link1!=null) return link1;
		else if (link0!=null) return link0;
		throw new IllegalStateException();
	}
	private List<Constraint> sort(List<Constraint> unsortedPropConstraints){
		
		java.util.Comparator comp =  new java.util.Comparator() {
			@Override
			public int compare(Object obj1, Object obj2) {	
				return getCost(obj1) - getCost(obj2);
			}
		};
		Collections.sort(unsortedPropConstraints, comp);
		List<Constraint> sortedConstraints = unsortedPropConstraints;
		return sortedConstraints;
		
	}
	
	private int getCost(Object obj){
		if(obj instanceof PropertyConstraint){
			PropertyConstraint<Vertex> vc = (PropertyConstraint<Vertex>) obj;
			Term[] terms = vc.getTerms();
			PropertyTerm key = (PropertyTerm) terms[0];
			ValueTerm value = (ValueTerm) terms[1];
			if(key.getKey().equals("isAbstract") && value.getValue().equals("true"))
				return 10; //for higher rank in sorting
			else
				return 0;
		}
		return 0;		
	}
}
