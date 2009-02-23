package nz.ac.massey.cs.gpl4jung.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPathUtils;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.utils.UserDataContainer;
import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.MotifInstance;
import nz.ac.massey.cs.gpl4jung.constraints.EdgeConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.PathConstraint;

public class MotifInstanceImpl implements MotifInstance {
	Motif motif = null;
	
	public Object getLink(LinkConstraint constraint, Graph g) {
		String source = constraint.getSource();
		String target = constraint.getTarget();
		Vertex s = this.getVertex(source);
		Vertex t = this.getVertex(target);
		
		if(constraint instanceof EdgeConstraint){
			return constraint.check(null, s, t);
		}
		else if (constraint instanceof PathConstraint){
			return constraint.check(g, s, t);
		}
		else
			return null;
	}
	
	public void setLink(Object link) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Motif getMotif() {
		return motif;
	}
	
	public void setMotif(Motif motif){
		this.motif = motif;
	}

	@Override
	public Vertex getVertex(String roleName) {
		for(Iterator itr=v.iterator();itr.hasNext();){
			Vertex role= (Vertex) itr.next();
			if(role.getUserDatum(key).equals(roleName))
				return role;
		}
		return null;
	}
	
	public void setVertex(Vertex v, String roleName){
		//this.key = key;
		v.setUserDatum(key, roleName, UserData.SHARED);
		this.v.add(v);
	}
private List<Vertex> v = new ArrayList<Vertex>();
String key = "name";





}
