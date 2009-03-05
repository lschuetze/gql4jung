package nz.ac.massey.cs.gpl4jung.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.AbstractSparseEdge;
import edu.uci.ics.jung.utils.UserDataContainer;

import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import nz.ac.massey.cs.gpl4jung.Constraint;
import nz.ac.massey.cs.gpl4jung.GQL;
import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.Path;

import nz.ac.massey.cs.gpl4jung.PropertyConstraint;
import nz.ac.massey.cs.gpl4jung.QueryOptimizer;
import nz.ac.massey.cs.gpl4jung.ResultListener;


public class GQLImpl implements GQL {
	ConstraintSchedulerImpl cs = new ConstraintSchedulerImpl();
	private boolean cancelled = false;
    
	@Override
	public void query(Graph graph, Motif motif, ResultListener listener) {
		
    	//gettting initial binding 
    	String role = motif.getRoles().get(0);
    	for(Object o:graph.getVertices()){
    		Vertex v = (Vertex) o;
    		List<Constraint> constraints = motif.getConstraints();
    		Bindings binding = new Bindings();
    		binding.bind(role, v);
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
		
		//check for termination
    	if (constraints.isEmpty()){
    		MotifInstanceImpl motifInstance = new MotifInstanceImpl();
    		motifInstance.addAll(replacement.asMap());
    		listener.found(motifInstance);
    		return;
    	}
		
		Constraint c = cs.selectNext(g, constraints, replacement);
		if(c instanceof PropertyConstraint){
			PropertyConstraint pc = (PropertyConstraint) c;
			String owner = pc.getOwner();
			Object instance = replacement.lookup(owner);
			if(instance instanceof UserDataContainer){
				UserDataContainer edgeOrVertex = (UserDataContainer) instance;
				if(pc.check(g, edgeOrVertex)){
					List<Constraint> newConstraints = copy(constraints);
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
			String source = lc.getSource();
			String target = lc.getTarget();
			Object instance1 = replacement.lookup(source);
			Object instance2 = replacement.lookup(target);
			if(instance1 == null && instance2 == null){
				throw new NullPointerException(); 
			}
			else if (instance1 == null && instance2 != null){
				//we have got target, so look for possible sources
				Vertex targetVertex = (Vertex) replacement.lookup(target);
				Iterator<ConnectedVertex<Edge>> ps = lc.getPossibleSources(g, targetVertex);
    			List<ConnectedVertex<Edge>> sources = IteratorUtils.toList(ps);
    			if(sources.size()!=0){
    				for(Iterator itr = sources.iterator();itr.hasNext();){
	    				ConnectedVertex nextInstance = (ConnectedVertex) itr.next();
	    				PropertyConstraint edgePropConstraint = lc.getEdgePropertyConstraint();
		    			Object instance =  nextInstance.getLink();
		    			if(instance instanceof AbstractSparseEdge){
		    				Edge e = (Edge) instance;
		    				if(edgePropConstraint!=null && edgePropConstraint.check(g,e)){
		    					if(!mustNotBinding(replacement, nextInstance.getVertex())){
			    					//add new replacements (link and vertex)
			    					Bindings nextReplacement = createBindingMap(replacement);
					    			nextReplacement.bind(source, nextInstance.getVertex());
					    			nextReplacement = createBindingMap(nextReplacement);
					    			String link = lc.getID();
					    			nextReplacement.bind(link, nextInstance.getLink());
					    			List<Constraint> newConstraints = copy(constraints);
					    			newConstraints.remove(lc);
					    			resolve(g,newConstraints,nextReplacement,listener);
					    			//release bindings (link and vertex)
					    			releaseBindingMap(nextReplacement);
					    			releaseBindingMap(nextReplacement);
			    				}
		    				}				
		    			}
	    				else if (instance instanceof Path){
	    					Path p = (Path) instance;
	    					List<Edge> list = p.getEdges(); 
	    					Edge[] path = getEdgesFromPath(list);
	    					if(edgePropConstraint!=null && edgePropConstraint.check(g, path)){
	    						if(!mustNotBinding(replacement, nextInstance.getVertex())){
			    					//add new replacement
			    					Bindings nextReplacement = createBindingMap(replacement);
					    			nextReplacement.bind(source, nextInstance.getVertex());
					    			nextReplacement = createBindingMap(nextReplacement);
					    			String link = lc.getID();
					    			nextReplacement.bind(link, nextInstance.getLink());
					    			List<Constraint> newConstraints = copy(constraints);
					    			newConstraints.remove(lc);
					    			resolve(g,newConstraints,nextReplacement,listener);
					    			//release bindings (link and vertex)
					    			releaseBindingMap(nextReplacement);
					    			releaseBindingMap(nextReplacement);
			    				}
	    					}		   
	    				} 				
	    			}
    			}
			}
			else if (instance1 != null && instance2 == null){
				//we have source, so look for possible targets
				Vertex sourceVertex = (Vertex) replacement.lookup(source);
				Iterator<ConnectedVertex<Edge>> pt = lc.getPossibleTargets(g, sourceVertex);
    			List<ConnectedVertex<Edge>> targets = IteratorUtils.toList(pt);
    			if(targets.size()!=0){
    				for(Iterator itr = targets.iterator();itr.hasNext();){
    					ConnectedVertex nextInstance = (ConnectedVertex) itr.next();
    					PropertyConstraint edgePropConstraint = lc.getEdgePropertyConstraint();
		    			Object instance =  nextInstance.getLink();
		    			if(instance instanceof AbstractSparseEdge){
		    				Edge e = (Edge) instance;
		    				if(edgePropConstraint!=null && edgePropConstraint.check(g,e)){
		    					if(!mustNotBinding(replacement, nextInstance.getVertex())){
		    						//add new replacement
			    					Bindings nextReplacement = createBindingMap(replacement);
					    			nextReplacement.bind(target, nextInstance.getVertex());
					    			nextReplacement = createBindingMap(nextReplacement);
					    			String link = lc.getID();
					    			nextReplacement.bind(link, nextInstance.getLink());
					    			List<Constraint> newConstraints = copy(constraints);
					    			newConstraints.remove(lc);
					    			resolve(g,newConstraints,nextReplacement,listener);
					    			//release bindings (link and vertex)
					    			releaseBindingMap(nextReplacement);
					    			releaseBindingMap(nextReplacement);
		    					}	
		    				}				
		    			}
	    				else if (instance instanceof Path){
	    					Path p = (Path) instance;
	    					List<Edge> list = p.getEdges(); 
	    					Edge[] path = getEdgesFromPath(list);
	    					if(edgePropConstraint!=null && edgePropConstraint.check(g, path)){
	    						if(!mustNotBinding(replacement, nextInstance.getVertex())){
		    						//add new replacement
			    					Bindings nextReplacement = createBindingMap(replacement);
					    			nextReplacement.bind(target, nextInstance.getVertex());
					    			nextReplacement = createBindingMap(nextReplacement);
					    			String link = lc.getID();
					    			nextReplacement.bind(link, nextInstance.getLink());
					    			List<Constraint> newConstraints = copy(constraints);
					    			newConstraints.remove(lc);
					    			resolve(g,newConstraints,nextReplacement,listener);
					    			//release bindings (link and vertex)
					    			releaseBindingMap(nextReplacement);
					    			releaseBindingMap(nextReplacement);
		    					}
	    					}		   
	    				}	
		    		}
    			}	
			}
			else if (instance1 != null && instance2 != null){
				Vertex sourceVertex = (Vertex) instance1;
				Vertex targetVertex = (Vertex) instance2;
				Object link = lc.check(g, sourceVertex, targetVertex);
				if(link!=null){
					String id = lc.getID();
					Bindings nextReplacement = createBindingMap(replacement);
					nextReplacement.bind(id, link);
					List<Constraint> newConstraints = copy(constraints);
					newConstraints.remove(lc);
					resolve(g,newConstraints,replacement,listener);
					//release binding (link only)
					releaseBindingMap(nextReplacement);
				}
				else
					return;
			}
		}
	}

	/**
	 * Retrieves edges from the list
	 * @param list
	 * @return an array of edges included in the path
	 */
	private Edge[] getEdgesFromPath(List<Edge> list) {
		Edge path[] = new Edge[list.size()];
		for(Iterator itr=list.iterator();itr.hasNext();){
			for(int i=0;i<list.size();i++){
				Edge e = (Edge) itr.next();
				path[i]= e;
			}
		}
		return path;
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
     * Check whether an instance can be added to the bindings.
     * @param bindings the existing bindings
     * @param object an object
     */
    private boolean mustNotBinding(Bindings bindings, Object object) {
    	return bindings.containsValue(object);
    }
    /**
     * Copy a list. 
     * @param list a list
     * @return a list
     */
    private List<Constraint> copy(List<Constraint> list) {
        List<Constraint> newList = new ArrayList<Constraint>();
        newList.addAll(list);
        return newList;
    }
}
