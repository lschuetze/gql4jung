package nz.ac.massey.cs.gpl4jung.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.IteratorUtils;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.AbstractSparseEdge;
import edu.uci.ics.jung.io.GraphMLFile;
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
import nz.ac.massey.cs.gpl4jung.constraints.ComplexPropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.NegatedPropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.Operator;
import nz.ac.massey.cs.gpl4jung.constraints.PropertyTerm;
import nz.ac.massey.cs.gpl4jung.constraints.SimplePropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.ValueTerm;
import nz.ac.massey.cs.processors.Processor;


public class GQLImpl implements GQL {
	ConstraintSchedulerImpl cs = new ConstraintSchedulerImpl();
	private boolean cancelled = false;

	private Motif motif = null;
	private int counter = 1;
    @Override
	public void cancel() {
		this.cancelled = true;
	}
    @Override
	public void reset() {
		this.cancelled = false;
	}
	@Override
	public void query(Graph graph, Motif motif, ResultListener listener) {
		this.motif = motif; 
		//looking for graph processing instruction in motif roles
		//first two roles in motif represent following
		//<graphprocessor class="nz.ac.massey.cs.processors.ClusterProcessor"/>
		if(motif.getRoles().contains("graphprocessor")){
			//getting class name for processor from motif
			String processorClass = motif.getRoles().get(1);
			//dynamically instatiating processor through java reflection
			Processor clusterProcessor=null;
			try {
				clusterProcessor = (Processor) Class.forName(processorClass).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			graph = clusterProcessor.process(graph);
			motif.getRoles().remove("graphprocessor");
			motif.getRoles().remove(processorClass);
		}
		//gettting initial binding 
    	String role = motif.getRoles().get(0);  	
    	for(Object o:graph.getVertices()){
    		Vertex v = (Vertex) o;
    		List<Constraint> constraints = motif.getConstraints();
    		cs.prepare(graph, constraints);
    		Bindings binding = new Bindings();
    		binding.bind(role, v);
    		resolve(graph, constraints, binding, listener);
	    }
	}

	@Override
	public void query(Graph graph, Motif motif, ResultListener listener,
			QueryOptimizer optimizer) {

	}
	
	public void resolve(Graph g, List<Constraint> constraints, Bindings replacement, ResultListener listener) {
		
		if(cancelled){
			return;
		}
		
		//check for termination
    	if (constraints.isEmpty()){
    		MotifInstanceImpl motifInstance = new MotifInstanceImpl();
    		motifInstance.setMotif(motif);
    		Map<String, Object> motifGraph = replacement.asMap();
    		//skipping if the classes are anonymous inner classes
    		List<String> roles = motif.getRoles();
    		for(String s:roles){
    			Vertex v = (Vertex) replacement.lookup(s);
    			String name = (String) v.getUserDatum("name");
    			if(Pattern.matches(".*\\$\\d",name)){
    				return;
    			}
    		}
    		motifInstance.addAll(motifGraph);
    		listener.found(motifInstance);
    	}
		Constraint c = cs.selectNext(g, constraints, replacement);
		if(c instanceof SimplePropertyConstraint){
			PropertyConstraint pc = (PropertyConstraint) c;
			String owner = pc.getOwner();
			Object instance = replacement.lookup(owner);
			if(instance instanceof UserDataContainer){
				UserDataContainer edgeOrVertex = (UserDataContainer) instance;
				if(pc instanceof SimplePropertyConstraint && pc.check(g, edgeOrVertex)){
					List<Constraint> newConstraints = copy(constraints);
					newConstraints.remove(pc);
					resolve(g,newConstraints,replacement,listener);
				}
				else
					return; //backtrack
			}	
		}
		//complex property constraints
		else if (c instanceof ComplexPropertyConstraint){
			ComplexPropertyConstraint cpc1 = (ComplexPropertyConstraint) c;
			ComplexPropertyConstraint cpc = (ComplexPropertyConstraint) cpc1.clone();
			List<PropertyConstraint> parts = new ArrayList<PropertyConstraint>();
			List list = cpc.getParts();
			List<Constraint> list1= copy(list);
			SimplePropertyConstraint spc = (SimplePropertyConstraint) list1.get(0);
			String owner = spc.getOwner(); 
			Object instance1 = replacement.lookup(owner);
			UserDataContainer edgeOrVertex1 = null;
			
			if(instance1 instanceof UserDataContainer){
				edgeOrVertex1 = (UserDataContainer) instance1;
				PropertyTerm term1 = (PropertyTerm) spc.getTerms()[0];
				if(spc.getTerms()[1]!=null){
					ValueTerm term2 = (ValueTerm) spc.getTerms()[1];
					spc.setTerms(term1,term2);
					parts.add(spc);
				}
				else {
					ValueTerm term2 = new ValueTerm(term1.getValue(edgeOrVertex1));
					spc.setTerms(term1,term2);
					parts.add(spc);
					
				}
			}
			SimplePropertyConstraint spc2 = (SimplePropertyConstraint) list1.get(1);
			String owner2 = spc2.getOwner();
			Object instance2 = replacement.lookup(owner2);
			UserDataContainer edgeOrVertex2 = null;
			if(instance2 instanceof UserDataContainer){
				edgeOrVertex2 = (UserDataContainer) instance2;
				PropertyTerm term1 = (PropertyTerm) spc2.getTerms()[0];
				if(spc2.getTerms()[1]!=null){
					ValueTerm term2 = (ValueTerm) spc2.getTerms()[1];
					spc2.setTerms(term1,term2);
					parts.add(spc2);
				}
				else{
					ValueTerm term2 = new ValueTerm(term1.getValue(edgeOrVertex2));
					spc2.setTerms(term1,term2);
					parts.add(spc2);
					
				}
			}
			
			cpc.setParts(parts);
			Operator op = Operator.getInstance("=");
			
			if(cpc.check(g, edgeOrVertex1, edgeOrVertex2)){
				List<Constraint> newConstraints = copy(constraints);
				newConstraints.remove(cpc1);
				resolve(g,newConstraints,replacement,listener);
			}
			else
				
				return; //backtrack
			
		}
		//negated complex property constraints
		else if (c instanceof  NegatedPropertyConstraint){
			NegatedPropertyConstraint npc = (NegatedPropertyConstraint) c;
			if(npc.getPart() instanceof SimplePropertyConstraint){
				SimplePropertyConstraint spc = (SimplePropertyConstraint) npc.getPart();
				String owner = spc.getOwner();
				Object instance1 = replacement.lookup(owner);
				UserDataContainer edgeOrVertex1 = null;
				if(instance1 instanceof UserDataContainer){
					npc.setPart(spc);
					UserDataContainer edgeOrVertex = (UserDataContainer) instance1;
					if(!npc.check(g, edgeOrVertex)){
						List<Constraint> newConstraints = copy(constraints);
						newConstraints.remove(npc);
						resolve(g,newConstraints,replacement,listener);
					}
					else
						return; //backtrack
				}	
			}
			else if (npc.getPart() instanceof ComplexPropertyConstraint){
				ComplexPropertyConstraint cpc = (ComplexPropertyConstraint) npc.getPart();
				List<PropertyConstraint> parts = new ArrayList<PropertyConstraint>();
				List list = cpc.getParts();
				List<Constraint> list1= copy(list);
				SimplePropertyConstraint spc = (SimplePropertyConstraint) list1.get(0);
				String owner = spc.getOwner(); 
				Object instance1 = replacement.lookup(owner);
				UserDataContainer edgeOrVertex1 = null;
				
				if(instance1 instanceof UserDataContainer){
					edgeOrVertex1 = (UserDataContainer) instance1;
					PropertyTerm term1 = (PropertyTerm) spc.getTerms()[0];
					if(spc.getTerms()[1]!=null){
						ValueTerm term2 = (ValueTerm) spc.getTerms()[1];
						spc.setTerms(term1,term2);
						parts.add(spc);
					}
					else {
						ValueTerm term2 = new ValueTerm(term1.getValue(edgeOrVertex1));
						spc.setTerms(term1,term2);
						parts.add(spc);
						
					}
				}
				SimplePropertyConstraint spc2 = (SimplePropertyConstraint) list1.get(1);
				String owner2 = spc2.getOwner();
				Object instance2 = replacement.lookup(owner2);
				UserDataContainer edgeOrVertex2 = null;
				if(instance2 instanceof UserDataContainer){
					edgeOrVertex2 = (UserDataContainer) instance2;
					PropertyTerm term1 = (PropertyTerm) spc2.getTerms()[0];
					if(spc2.getTerms()[1]!=null){
						ValueTerm term2 = (ValueTerm) spc2.getTerms()[1];
						spc2.setTerms(term1,term2);
						parts.add(spc2);
					}
					else{
						ValueTerm term2 = new ValueTerm(term1.getValue(edgeOrVertex2));
						spc2.setTerms(term1,term2);
						parts.add(spc2);
						
					}
				}
				
				cpc.setParts(parts);
				npc.setPart(cpc);
				if(npc.check(g, edgeOrVertex1, edgeOrVertex2)){
					List<Constraint> newConstraints = copy(constraints);
					newConstraints.remove(npc);
					resolve(g,newConstraints,replacement,listener);
				}
				else
					return; //backtrack
			}
			
		}
		
		//resolving constraints for binary constrainst
		else if (c instanceof LinkConstraint){
			LinkConstraint lc = (LinkConstraint) c;
			String source = lc.getSource().toString();
			String target = lc.getTarget().toString();
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
		    				
	    					else if (edgePropConstraint==null){
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
	    					
	    					else if (edgePropConstraint==null){
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
		    			//added for group constraint,
	    				else if (instance == null){
	    					if(!mustNotBinding(replacement, nextInstance.getVertex())){
		    					//add new replacement
		    					Bindings nextReplacement = createBindingMap(replacement);
				    			nextReplacement.bind(source, nextInstance.getVertex());
				    			List<Constraint> newConstraints = copy(constraints);
				    			newConstraints.remove(lc);
				    			resolve(g,newConstraints,nextReplacement,listener);
				    			releaseBindingMap(nextReplacement);
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
		    				//added
		    				else if (edgePropConstraint==null){
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
	    					//added
		    				else if (edgePropConstraint==null){
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
		    			//added for group constraint,
	    				else if (instance == null){
	    					if(!mustNotBinding(replacement, nextInstance.getVertex())){
		    					//add new replacement
		    					Bindings nextReplacement = createBindingMap(replacement);
				    			nextReplacement.bind(target, nextInstance.getVertex());
				    			List<Constraint> newConstraints = copy(constraints);
				    			newConstraints.remove(lc);
				    			resolve(g,newConstraints,nextReplacement,listener);
				    			releaseBindingMap(nextReplacement);
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
        Object[] newArray = list.toArray();
        Object[] clonedArray = newArray.clone();
        for(int i = 0; i<newArray.length;i++){
        	clonedArray[i]= newArray.clone()[i];
        }
        for(int i = 0;i<clonedArray.length;i++){
        	Constraint c = (Constraint) clonedArray[i];
        	newList.add(i, c);
        }
       return newList;
    }
}
