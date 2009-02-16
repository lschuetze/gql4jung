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
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.Path;
import nz.ac.massey.cs.gpl4jung.PropertyConstraint;
import nz.ac.massey.cs.gpl4jung.QueryOptimizer;
import nz.ac.massey.cs.gpl4jung.ResultListener;
import nz.ac.massey.cs.gpl4jung.constraints.EdgeConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.PathConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.PropertyTerm;
import nz.ac.massey.cs.gpl4jung.constraints.Term;
import nz.ac.massey.cs.gpl4jung.constraints.ValueTerm;

public class GQLImpl implements GQL {
	
	private ConstraintScheduler constraintScheduler = new ConstraintSchedulerImpl();
	private boolean cancelled = false;
    private int counter = 0;
    private int instanceCounter = 0;
    private Graph graph = null;
	
	@Override
	public void query(Graph graph, Motif motif, ResultListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void query(Graph graph, Motif motif, ResultListener listener,
			QueryOptimizer optimizer) {
		// TODO Auto-generated method stub

	}
	
	public void solveConstraints(List<Constraint> constraints, Bindings bindings){
		counter++;
		if(cancelled){
			return;
		}
		for(Iterator itr=constraints.iterator();itr.hasNext();){
			Object c = (Object) itr.next();
			if (c instanceof PropertyConstraint){
				PropertyConstraint constraint = (PropertyConstraint) c;
				Term[] terms = constraint.getTerms();
				PropertyTerm key = (PropertyTerm) terms[0];
				ValueTerm value = (ValueTerm) terms[1];
				Object instance = bindings.lookup(key.getKey());
				if (instance==null){
					// not yet binded
					Bindings nextBinding = createBindingMap(bindings);
					// add new binding
					nextBinding.bind(key.getKey(), value.getValue());
					releaseBindingMap(nextBinding);
				}
			}
			else if (c instanceof EdgeConstraint){
				EdgeConstraint edgeConstraint = (EdgeConstraint) c;
				String source = edgeConstraint.getSource();
				String target = edgeConstraint.getTarget();
				Vertex sourceVertex = getVertexFromGraph(graph, source);
				Vertex targetVertex = getVertexFromGraph(graph, target);
				Object instance1 = bindings.lookup(source);
				Object instance2 = bindings.lookup(target);
				if(instance1==null && instance2!=null){
					Iterator<ConnectedVertex<Edge>> possibleSources = edgeConstraint.getPossibleSources(graph, targetVertex);
					List<ConnectedVertex<Edge>> sources = IteratorUtils.toList(possibleSources); //list of all possible sources
				}
				else if (instance1!=null && instance2==null){
					Iterator<ConnectedVertex<Edge>> possibleTargets =  edgeConstraint.getPossibleTargets(graph, sourceVertex);
					List<ConnectedVertex<Edge>> targets = IteratorUtils.toList(possibleTargets); //list of all possible targets
				}
				else if (instance1!=null && instance2!=null){
					if(edgeConstraint.check(graph, sourceVertex, targetVertex)!=null){
						
					}
				}
			}
			else if (c instanceof PathConstraint){
				PathConstraint pathConstraint = (PathConstraint) c;
				String source = pathConstraint.getSource();
				String target = pathConstraint.getTarget();
				Vertex sourceVertex = getVertexFromGraph(graph, source);
				Vertex targetVertex = getVertexFromGraph(graph, target);
				Object instance1 = bindings.lookup(source);
				Object instance2 = bindings.lookup(target);
				if(instance1==null && instance2!=null){
					Iterator<ConnectedVertex<Path>> possibleSources = pathConstraint.getPossibleSources(graph, targetVertex);
					List<ConnectedVertex<Path>> sources = IteratorUtils.toList(possibleSources); //list of all possible sources
				}
				else if (instance1!=null && instance2==null){
					Iterator<ConnectedVertex<Path>> possibleTargets =  pathConstraint.getPossibleTargets(graph, sourceVertex);
					List<ConnectedVertex<Path>> targets = IteratorUtils.toList(possibleTargets); //list of all possible targets
				}
				else if (instance1!=null && instance2!=null){
					if(pathConstraint.check(graph, sourceVertex, targetVertex)!=null){
						
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
