package nz.ac.massey.cs.gpl4jung.impl;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.utils.UserDataContainer;
import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.MotifInstance;

public class MotifInstanceImpl implements MotifInstance {
	Motif motif = null;
	@Override
	public Object getLink(LinkConstraint constraint) {
		// TODO Auto-generated method stub
		return null;
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
		if(v.getUserDatum(key).equals(roleName)){
			return v;
		}
		return null;
	}
	
	public void setVertex(Vertex v, String key, String roleName){
		this.key = key;
		this.v = v;
		this.v.setUserDatum(key,roleName,UserData.SHARED);
	}
private Vertex v = null;
String key = null;




}
