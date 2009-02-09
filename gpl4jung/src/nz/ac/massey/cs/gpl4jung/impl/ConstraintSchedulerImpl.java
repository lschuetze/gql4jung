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
		List<Constraint> constraints = new ArrayList<Constraint>();
		for(Iterator itr=agenda.iterator();itr.hasNext();){
			Object c = (Object) itr.next();
			if(c instanceof PropertyConstraint){
				SimplePropertyConstraint<Vertex> vc = (SimplePropertyConstraint<Vertex>) c;
				Term[] terms = vc.getTerms();
				PropertyTerm key = (PropertyTerm) terms[0];
				ValueTerm value = (ValueTerm) terms[1];
				Object instance = bindings.lookup(key.getKey());
				if (instance == null) {
	                // not yet binded
					Bindings nextBinding = createBindingMap(bindings);
                    // add new binding
					nextBinding.bind(key.getKey(), value.getValue());
                    selectNext(g, agenda,nextBinding);
                    releaseBindingMap(nextBinding);
				}
				else {
	               
	                	selectNext(g, agenda,bindings);
				}
			}
			else if (c instanceof EdgeConstraint){
				Vertex v = getVertexFromGraph(g, ((EdgeConstraint) c).getSource());
				Iterator<ConnectedVertex<Edge>> pt =  ((EdgeConstraint) c).getPossibleTargets(g, v);
				List<ConnectedVertex<Edge>> list = IteratorUtils.toList(pt); //list of all possible targets
				//TODO: Setting up bindings
			}
		}
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
	
	/**
     * Create the next map containing bindings.
     * @param map a map containing binding
     * @return a map
     */
    private Bindings createBindingMap(Bindings bindings) {
    	bindings.gotoChildLevel();
    	return bindings;
    }

    /**
     * Release the map containing bindings. 
     * @param map a map containing bindings.
     */
    private void releaseBindingMap(Bindings bindings) {
    	bindings.gotoParentLevel();
    }

    /**
     * Check whether a binding map contains an object. 
     * @param map a binding map
     * @param obj an object
     * @return a boolean
     */
    private boolean containsValue(Bindings map, Object value) {
        return map.containsValue(value);
    }
    
    private Vertex getVertexFromGraph(Graph g, String vertexname){
		String key="name";
		Vertex vert = null;
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();){
			Vertex v = (Vertex) iter.next();
			if(v.getUserDatum(key).equals(vertexname)){
				vert = (Vertex) v;
			}
		}
		return vert;
	}
}
