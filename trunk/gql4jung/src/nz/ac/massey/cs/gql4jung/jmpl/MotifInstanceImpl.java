package nz.ac.massey.cs.gql4jung.jmpl;

import java.util.HashMap;
import java.util.Map;
import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.gql4jung.LinkConstraint;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;

/**
 * Motif instance implementation.
 * @author jens dietrich
 */

public class MotifInstanceImpl implements MotifInstance {
	
	private Motif motif = null;
	private Map<String,Vertex> vertexBindings = new HashMap<String, Vertex>();
	private Map<LinkConstraint,Object> linkBindings = new HashMap<LinkConstraint,Object>();
	
	MotifInstanceImpl(Motif motif,Bindings bindings) {
		this.motif = motif;
		this.vertexBindings.putAll(bindings.getRoleBindingsAsMap());
		this.linkBindings.putAll(bindings.getLinkBindingsAsMap());
		//System.out.println("result created: " + this);
	}
	
	public Object getLink(LinkConstraint constraint) {
		return linkBindings.get(constraint);
	}
		
	@Override
	public Motif getMotif() {
		return motif;
	}
	
	@Override
	public Vertex getVertex(String role) {
		return vertexBindings.get(role);
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("aMotifInstance(");
		boolean f = true;
		for (Map.Entry e:this.vertexBindings.entrySet()) {
			if (f) f=false;
			else b.append(',');
			b.append(e.getKey());
			b.append("->");
			b.append(e.getValue());
		}
		b.append(")");
		return b.toString();
	}
}
