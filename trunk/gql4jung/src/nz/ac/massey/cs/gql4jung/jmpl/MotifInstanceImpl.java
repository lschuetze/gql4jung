package nz.ac.massey.cs.gql4jung.jmpl;

import java.util.HashMap;
import java.util.Map;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Vertex;

/**
 * Motif instance implementation.
 * @author jens dietrich
 */

public class MotifInstanceImpl extends Logging implements MotifInstance {
	
	private Motif motif = null;
	private Map<String,Vertex> vertexBindings = new HashMap<String, Vertex>();
	private Map<String,Path> pathBindings = new HashMap<String,Path>();
	
	MotifInstanceImpl(Motif motif,Bindings bindings) {
		this.motif = motif;
		this.vertexBindings.putAll(bindings.getRoleBindingsAsMap());
		this.pathBindings.putAll(bindings.getPathBindingsAsMap());
		if (LOG_INST.isDebugEnabled()) {
			LOG_INST.debug("result created: " + this);
		}
	}
	
	public Path getPath(String roleName) {
		return pathBindings.get(roleName);
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
