package nz.ac.massey.cs.gpl4jung.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import nz.ac.massey.cs.gpl4jung.Constraint;
import nz.ac.massey.cs.gpl4jung.GQL;
import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.Motif;

import nz.ac.massey.cs.gpl4jung.PropertyConstraint;
import nz.ac.massey.cs.gpl4jung.QueryOptimizer;
import nz.ac.massey.cs.gpl4jung.ResultListener;


public class GQLImpl implements GQL {
	
	private boolean cancelled = false;
    	
	@Override
	public void query(Graph graph, Motif motif, ResultListener listener) {
		List<Constraint> constraints = motif.getConstraints();
    	//gettting initial binding 
    	String role = motif.getRoles().get(0);
    	for(Object o:graph.getVertices()){
    		Vertex v = (Vertex) o;
    		Bindings binding = new Bindings();
    		binding.bind(role, v);
    		//motifInstance.setVertex(v, role);
    		//source = v;
    		resolve(graph, constraints, binding, listener);
    	}
	}

	@Override
	public void query(Graph graph, Motif motif, ResultListener listener,
			QueryOptimizer optimizer) {
		// TODO Auto-generated method stub

	}
	
public void resolve(Graph g, List<Constraint> constraints, Bindings replacement, ResultListener listener) {
		
		if(cancelled){
			return;
		}
		MotifInstanceImpl motifInstance = new MotifInstanceImpl();
		//check for termination
    	if (constraints.isEmpty()){
    		//TODO: instantiate motifInstance
    		listener.found(motifInstance);
    		return;
    	}
		ConstraintSchedulerImpl cs = new ConstraintSchedulerImpl();
		Constraint c = cs.selectNext(g,constraints,replacement);
		if(c instanceof PropertyConstraint){
			PropertyConstraint pc = (PropertyConstraint) c;
			if (pc!=null){
				Vertex v = (Vertex) replacement.lookup(pc.getOwner());
				if(pc.check(g, v)){
					List<Constraint> newConstraints = constraints;
					newConstraints.remove(pc);
					resolve(g,newConstraints,replacement,listener);
				}
				else
					return; //backtrack
			}
		}
		//resolving constraints for binary constrainst
		else if (c instanceof LinkConstraint){
			LinkConstraint lc = (LinkConstraint) c;
			if(lc!=null){
				String source = lc.getSource();
				String target = lc.getTarget();
				Object instance1 = replacement.lookup(source);
				Object instance2 = replacement.lookup(target);
				if (instance1 == null && instance2 != null){
					//we have got target so look for possible sources
					Vertex targetVertex = (Vertex) replacement.lookup(target);
					Iterator<ConnectedVertex<Edge>> ps = lc.getPossibleSources(g, targetVertex);
	    			List<ConnectedVertex<Edge>> sources = IteratorUtils.toList(ps);
	    			for(Iterator itr = sources.iterator();itr.hasNext();){
	    				Object nextInstance = itr.next();
	    				if(!mustNotBinding(replacement, nextInstance)){
	    					//add new replacement
	    					Bindings nextReplacement = createBindingMap(replacement);
			    			nextReplacement.bind(source, nextInstance);
			    			List<Constraint> newConstraints = constraints;
			    			newConstraints.remove(lc);
			    			resolve(g,newConstraints,nextReplacement,listener);
	    				}
	    				 				
	    			}
	    			
				}
				else if (instance1 != null && instance2 == null){
					//we have source, so look for possible targets
					Vertex sourceVertex = (Vertex) replacement.lookup(source);
					Iterator<ConnectedVertex<Edge>> pt = lc.getPossibleTargets(g, sourceVertex);
	    			List<ConnectedVertex<Edge>> targets = IteratorUtils.toList(pt);
	    			for(Iterator itr = targets.iterator();itr.hasNext();){
	    				Object nextInstance = itr.next();
	    				if(!mustNotBinding(replacement, nextInstance)){
	    					//add new replacement
	    					Bindings nextReplacement = createBindingMap(replacement);
			    			nextReplacement.bind(target, nextInstance);
			    			List<Constraint> newConstraints = constraints;
			    			newConstraints.remove(lc);
			    			resolve(g,newConstraints,nextReplacement,listener);
	    				} 				
	    			}
				}
				else if (instance1 != null && instance2 != null){
					if(lc.check(g, (Vertex) instance1, (Vertex)instance2)!=null){
						List<Constraint> newConstraints = constraints;
						newConstraints.remove(lc);
						resolve(g,newConstraints,replacement,listener);
					}
				}
			}
		}
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
    
    /**
     * Check whether an instance can be added to the bindings.
     * @param bindings the existing bindings
     * @param object an object
     */
    private boolean mustNotBinding(Bindings bindings, Object object) {
    	return this.containsValue(bindings, object);
    }
    
	
	
	
}
