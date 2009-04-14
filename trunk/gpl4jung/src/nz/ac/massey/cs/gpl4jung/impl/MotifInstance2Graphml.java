package nz.ac.massey.cs.gpl4jung.impl;

import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.AbstractSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphFile;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.UserData;
import nz.ac.massey.cs.gpl4jung.Constraint;
import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.MotifInstance;
import nz.ac.massey.cs.gpl4jung.Path;
/**
 * Adapter class for converting motif instances to graphml files
 * @author Ali
 *
 */
public class MotifInstance2Graphml {
	private MotifInstance mi; 
	Graph graph;
	GraphFile gm;
	public MotifInstance2Graphml(){
		this.mi = null;
		graph = new DirectedSparseGraph();
		gm = new GraphMLFile();
	}
	public void convert(MotifInstance motifInstance, String path){
		this.mi = motifInstance;
		Motif motif = mi.getMotif();
		//annotating graph with role and core attributes. Copying vertices to graph from motif instance
		for(Iterator iter=motif.getRoles().iterator();iter.hasNext();){
			String role = (String) iter.next();
			Vertex v1 = mi.getVertex(role);
			Vertex v = (Vertex) v1.copy(graph);
			if(v!=null){
				v.addUserDatum("role", role, UserData.SHARED);
			}
			if(motif.isCore(role)){
				v.addUserDatum("core", "true", UserData.SHARED);
			}
			else {
				v.addUserDatum("core", "false", UserData.SHARED);
			}
		}
		//copying edges in graph from motif instance, 
		for(Iterator iter = motif.getConstraints().iterator();iter.hasNext();){
			Constraint c = (Constraint) iter.next();
			if(c instanceof LinkConstraint){
				LinkConstraint constraint = (LinkConstraint)c;
				Object link = mi.getLink(constraint);
				//if path convert to edges then add to graph.
				//Add any vertices coming in the path to graph, if not already added
				if(link instanceof Path){
					Path p = (Path)link;
					List<Edge> edges = p.getEdges();
					for(int i=0;i<edges.size();i++){
						Edge e = edges.get(i); 
						Vertex source = (Vertex) e.getEndpoints().getFirst();
						Vertex target = (Vertex) e.getEndpoints().getSecond();
						boolean v1 = graph.getVertices().contains(source);
						boolean v2 = graph.getVertices().contains(target);
						boolean e1 = graph.getEdges().contains(e);
						if(v1 && v2 && !e1){
							e.copy(graph);
						}
						else if(v1 && !v2 && !e1){
							target.copy(graph);
							e.copy(graph);
						}
						else if(!v1 && v2 && !e1){
							source.copy(graph);
							e.copy(graph);
						}
					}
				} else if(link instanceof AbstractSparseEdge) {
					Edge e = (Edge) link;
					if(!graph.getEdges().contains(e))
						e.copy(graph);
				}
			}
		}
			
		//adding result file to its respective folder. 
		gm.save(graph, path);
	}
}
