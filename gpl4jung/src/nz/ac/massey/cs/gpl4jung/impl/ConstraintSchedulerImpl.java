package nz.ac.massey.cs.gpl4jung.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

import nz.ac.massey.cs.gpl4jung.Constraint;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.PropertyConstraint;
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
			if(c instanceof PathConstraint){
				interimConstraints.add(c);
			}
		}
		List<Constraint> sortedConstraints = sort(interimConstraints);
		return sortedConstraints;
	}

	@Override
	public Constraint selectNext(Graph g, List<Constraint> agenda, Bindings bindings) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Constraint> sort(List<Constraint> unsortedPropConstraints){
		
		java.util.Comparator comp =  new java.util.Comparator() {
			@Override
			public int compare(Object obj1, Object obj2) {	
				if(obj1 instanceof PropertyConstraint && obj2 instanceof PropertyConstraint){
					SimplePropertyConstraint<Vertex> vc1 = (SimplePropertyConstraint<Vertex>) obj1;
					SimplePropertyConstraint<Vertex> vc2 = (SimplePropertyConstraint<Vertex>) obj2;
					Term[] terms1 = vc1.getTerms();
					Term[] terms2 = vc2.getTerms();
					PropertyTerm key1 = (PropertyTerm) terms1[0];
					ValueTerm value1 = (ValueTerm) terms1[1];
					PropertyTerm key2 = (PropertyTerm) terms2[0];
					ValueTerm value2 = (ValueTerm) terms2[1];
					if(key1.getKey().equals("isAbstract") && value1.getValue().equals("true"))
						return 1; //for higher rank in sorting
					else
						if(key2.getKey().equals("isAbstract") && value2.getValue().equals("true"))
							return -1; //for higher rank in sorting
						else
							return 0;
				}
				return 0;
			}
		};
		Collections.sort(unsortedPropConstraints, comp);
		List<Constraint> sortedConstraints = unsortedPropConstraints;
		return sortedConstraints;
		
	}
}
