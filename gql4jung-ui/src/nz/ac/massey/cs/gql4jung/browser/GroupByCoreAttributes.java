package nz.ac.massey.cs.gql4jung.browser;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.MotifInstance;

import edu.uci.ics.jung.graph.Vertex;

public class GroupByCoreAttributes implements VertexGroupByDefinition {

	@Override
	public Object getGroupIdentifier (MotifInstance instance) {
		final Motif motif = instance.getMotif();
		StringBuffer b = new StringBuffer(); 
		String SEP = "___";
		Predicate<String> coreFilter = new Predicate<String>() {
			@Override
			public boolean apply(String role) {
				return motif.isCore(role);
			}
		};
		boolean first = true;
		Iterator<String> coreRoles = Iterators.filter(motif.getRoles().iterator(), coreFilter);
		while (coreRoles.hasNext()) {
			if (first) first = false;
			else b.append(SEP);
			String role = coreRoles.next();
			Vertex v = instance.getVertex(role);
			// TODO check whether this attribute exists and is unique
			b.append(v.getUserDatum("namespace"));
			b.append('.');
			b.append(v.getUserDatum("name"));
		}
		
		//System.out.println("key: " + instance + " -> " + b);
		return b.toString();
	}

}
