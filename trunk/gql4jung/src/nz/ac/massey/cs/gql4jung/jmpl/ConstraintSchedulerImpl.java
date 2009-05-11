package nz.ac.massey.cs.gql4jung.jmpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import edu.uci.ics.jung.graph.Graph;
import nz.ac.massey.cs.gql4jung.Constraint;
import nz.ac.massey.cs.gql4jung.LinkConstraint;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.PropertyConstraint;
import nz.ac.massey.cs.gql4jung.constraints.EdgeConstraint;
import nz.ac.massey.cs.gql4jung.constraints.PathConstraint;

/**
 * Simple constraint scheduler.
 * @author jens dietrich
 */

public class ConstraintSchedulerImpl extends Logging implements ConstraintScheduler {

	@Override
	public List<Constraint> getConstraints(Graph graph,Motif motif) {
		
		assert motif.getRoles().size()>0;
		
		Collection<String> bindings = new HashSet<String>(); // keep track of bound roles
		Collection<String> roles = motif.getRoles();
		List<Constraint> pool = prepare(motif.getConstraints());
		List<Constraint> optimised = new ArrayList<Constraint>(pool.size());
		
		Constraint next = null;
		while ((next = selectNext(graph,motif,pool,optimised,bindings))!=null) {
			pool.remove(next);
			optimised.add(next);
			LOG_SCHED.debug("Added constraint: "+next);
		}
		assert optimised.size() == motif.getConstraints().size();
		if (optimised.size() != motif.getConstraints().size()) {
			LOG_SCHED.warn("Not all constraints have been scheduled");
		}
		return optimised; 

	}

	private Constraint selectNext(Graph graph, Motif motif, List<Constraint> pool,	List<Constraint> optimised, Collection<String> bindings) {
		if (pool.size()==0) return null;
		
		// find initial constraint
		if (bindings.size()==0) {	
			for(Constraint c:pool){
				if(c instanceof PropertyConstraint){
					PropertyConstraint pc = (PropertyConstraint)c;
					if (pc.getOwner()!=null && motif.getRoles().contains(pc.getOwner())) {
						bind(bindings,pc.getOwner());
						return pc;
					}
				}
			}
			// nothing found, just make an assertion
			bind(bindings,motif.getRoles().get(0));
		}
		
		// try to find property for role(s) already bound
		for (Iterator<PropertyConstraint> iter = getPropertyConstraints(pool);iter.hasNext();) {
			PropertyConstraint pc = iter.next();
			if (bindings.containsAll(pc.getOwnerRoles())) {
				return pc;
			}
		}
		

		// try to find link with both ends known
		for (Iterator<LinkConstraint> iter = getLinkConstraints(pool);iter.hasNext();) {
			LinkConstraint lc = iter.next();
			if (bindings.contains(lc.getSource()) && bindings.contains(lc.getTarget())) {
				return lc;
			}
		}
		
		// try to find link with source known
		for (Iterator<LinkConstraint> iter = getLinkConstraints(pool);iter.hasNext();) {
			LinkConstraint lc = iter.next();
			if (bindings.contains(lc.getSource())) {
				bind(bindings,lc.getTarget());
				return lc;
			}
		}
		
		// try to find link with target known
		for (Iterator<LinkConstraint> iter = getLinkConstraints(pool);iter.hasNext();) {
			LinkConstraint lc = iter.next();
			if (bindings.contains(lc.getTarget())) {
				bind(bindings,lc.getSource());
				return lc;
			}
		}
		
		// try to find property for (some) roles already bound - those are "inter-role" properties
		for (Iterator<PropertyConstraint> iter = getPropertyConstraints(pool);iter.hasNext();) {
			PropertyConstraint pc = iter.next();
			boolean ok = false;
			List<String> roles = pc.getOwnerRoles();
			for (String role:roles) {
				if (bindings.contains(role)) {
					ok = true;
				}
			}
			if (ok) {
				// add new roles - this will be a role bound by the constraint
				for (String role:roles) {
					if (!bindings.contains(role)) {
						bind(bindings,role);
					}
				}
				return pc;
			}
		}
		
		LOG_SCHED.warn("None of the scheduling rules can be applied, adding first in list: "+pool.get(0));
		return pool.get(0);
	}
	
	
	private Iterator<PropertyConstraint> getPropertyConstraints(List<Constraint> list) {
		Predicate<Constraint> filter = new Predicate<Constraint>() {
			@Override
			public boolean apply(Constraint c) {
				return c instanceof PropertyConstraint;
			}
		};
		Function<Constraint,PropertyConstraint> function = new Function<Constraint,PropertyConstraint>() {
			@Override
			public PropertyConstraint apply(Constraint c) {
				return (PropertyConstraint)c;
			}
		};
		return  Iterators.transform(
					Iterators.filter(list.iterator(),filter), 
					function);
	}
	
	private Iterator<LinkConstraint> getLinkConstraints(List<Constraint> list) {
		Predicate<Constraint> filter = new Predicate<Constraint>() {
			@Override
			public boolean apply(Constraint c) {
				return c instanceof LinkConstraint;
			}
		};
		Function<Constraint,LinkConstraint> function = new Function<Constraint,LinkConstraint>() {
			@Override
			public LinkConstraint apply(Constraint c) {
				return (LinkConstraint)c;
			}
		};
		return  Iterators.transform(
					Iterators.filter(list.iterator(),filter), 
					function);
	}
	
	private List<Constraint> prepare(List<Constraint> constraints) {
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
		return interimConstraints;
	}
	private void bind(Collection<String> bindings,String role) {
		this.LOG_SCHED.debug("registering binding: "+role);
		bindings.add(role);
	}
/*
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
	*/
}
