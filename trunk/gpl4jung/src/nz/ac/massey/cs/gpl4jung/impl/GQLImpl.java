package nz.ac.massey.cs.gpl4jung.impl;

import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.gpl4jung.Constraint;
import nz.ac.massey.cs.gpl4jung.GQL;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.QueryOptimizer;
import nz.ac.massey.cs.gpl4jung.ResultListener;
import nz.ac.massey.cs.gpl4jung.constraints.EdgeConstraint;

public class GQLImpl implements GQL {

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
//        // not yet binded
//		Bindings nextBinding = createBindingMap(bindings);
//        // add new binding
//		nextBinding.bind(key.getKey(), value.getValue());
//        selectNext(g, agenda,nextBinding);
//        releaseBindingMap(nextBinding);
		
//		else {
//        
//         	selectNext(g, agenda,bindings);
//
//		else if (c instanceof EdgeConstraint){
//			Vertex v = getVertexFromGraph(g, ((EdgeConstraint) c).getSource());
//			Iterator<ConnectedVertex<Edge>> pt =  ((EdgeConstraint) c).getPossibleTargets(g, v);
//			List<ConnectedVertex<Edge>> list = IteratorUtils.toList(pt); //list of all possible targets
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
