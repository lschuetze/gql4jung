package nz.ac.massey.cs.gql4jung;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.AbstractSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphFile;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.*;
import edu.uci.ics.jung.utils.UserDataContainer.CopyAction;
import edu.uci.ics.jung.utils.UserDataContainer.CopyAction.Clone;
/**
 * Adapter class for converting motif instances to graphml files
 * @author jens dietrich (completely redesigned, old version did fail when same vertex instantiated different roles)
 */
public class MotifInstance2Graph {

		
	public MotifInstance2Graph(){
	}
	
	public Graph asGraph(MotifInstance instance){
		
		Graph graph = new DirectedSparseGraph();
		Motif motif = instance.getMotif();
		
		Map<Vertex,Vertex> vertexMap = new HashMap<Vertex,Vertex>(); 
		// vertices can occur in multiple roles - therefore make sure we add them only once
		//annotating graph with role and core attributes. Copying vertices to graph from motif instance
		for(String role:motif.getRoles()){
			Vertex vOld = instance.getVertex(role);
			getOrAdd(graph,vOld,vertexMap,role);
		}
		//copying edges in graph from motif instance, 
		for(Constraint c:motif.getConstraints()){
			if(c instanceof LinkConstraint){
				Object link = instance.getLink((LinkConstraint)c);
				if(link instanceof Path){
					Path p = (Path)link;
					for(Edge e:p.getEdges()){
						createEdge(graph,e,vertexMap);
					}
				} else if(link instanceof AbstractSparseEdge) {
					Edge e = (Edge) link;
					createEdge(graph,e,vertexMap);
				}
			}
		}
		return graph;
	}
	private void createEdge(Graph graph, Edge e, Map<Vertex, Vertex> vertexMap) {
		Vertex newSource = getOrAdd(graph,(Vertex)e.getEndpoints().getFirst(),vertexMap,null);
		Vertex newTarget = getOrAdd(graph,(Vertex)e.getEndpoints().getSecond(),vertexMap,null);
		Edge newEdge = new DirectedSparseEdge(newSource,newTarget);
		graph.addEdge(newEdge);
		for (Iterator iter = e.getUserDatumKeyIterator();iter.hasNext();) {
			Object key = iter.next();
			newEdge.setUserDatum(key,e.getUserDatum(key),UserData.CLONE);
		}
	}

	private Vertex getOrAdd(Graph graph, Vertex vOld,Map<Vertex, Vertex> vertexMap, String role) {
		Vertex vNew = vertexMap.get(vOld);
		if (vNew==null) {
			vNew = (Vertex) vOld.copy(graph);
			vertexMap.put(vOld,vNew);
		}
		if (role!=null) {
			String v = (String) vNew.getUserDatum("role");
			if (v==null) v = role;
			else v = v+","+role;
			vNew.setUserDatum("role",v,UserData.CLONE);
		}
		return vNew;
		
	}




}
