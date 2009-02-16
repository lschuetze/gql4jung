package nz.ac.massey.cs.gpl4jung.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import nz.ac.massey.cs.gpl4jung.Constraint;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.Path;
import nz.ac.massey.cs.gpl4jung.PropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.EdgeConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.PathConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.PropertyTerm;
import nz.ac.massey.cs.gpl4jung.constraints.SimplePropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.Term;
import nz.ac.massey.cs.gpl4jung.constraints.ValueTerm;


public class ConstraintSchedulerImpl implements ConstraintScheduler {

	@Override
	public List<Constraint> getConstraints(Motif motif) {
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
		List<Constraint> sortedConstraints = sort(interimConstraints);
		return sortedConstraints;
	}

	@Override
	public Constraint selectNext(Graph g, List<Constraint> agenda, Bindings bindings) {
		
		for(Iterator itr=agenda.iterator();itr.hasNext();){
			Object c = (Object) itr.next();
			if(c instanceof PropertyConstraint){
				PropertyConstraint constraint = (PropertyConstraint) c;
				Term[] terms = constraint.getTerms();
				PropertyTerm key = (PropertyTerm) terms[0];
				ValueTerm value = (ValueTerm) terms[1];
				Object instance = bindings.lookup(key.getKey());
				if (instance!=null){
					if (instance.toString().equals(value.getValue().toString())) 
						return constraint;
				}
			}
			else if (c instanceof PathConstraint){
				PathConstraint pathConstraint = (PathConstraint) c;
				// what kind of bindings to look for 
				return pathConstraint;
			}
			else if (c instanceof EdgeConstraint){
				EdgeConstraint edgeConstraint = (EdgeConstraint) c;
				// what kind of bindings to look for 
				return edgeConstraint;
			}
		}
		return null;
	}
	public List<Constraint> sort(List<Constraint> unsortedPropConstraints){
		
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
	
	public int getCost(Object obj){
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
