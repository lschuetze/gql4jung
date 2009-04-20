package nz.ac.massey.cs.gql4jung.util;

import java.util.Collection;
import nz.ac.massey.cs.gql4jung.GroupByClause;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import edu.uci.ics.jung.graph.Vertex;

/**
 * Motif instance aggregation based on a collection of group by clauses 
 * consisting of vertex role names and optional attributes.
 * @author Jens Dietrich
 */
public class GroupByAggregation implements MotifInstanceAggregation {

	@Override
	public Object getGroupIdentifier (MotifInstance instance) {
		final Motif motif = instance.getMotif();
		Collection<GroupByClause> clauses = motif.getGroupByClauses();
		
		StringBuffer b = new StringBuffer(); 
		String SEP = "___";
		boolean first = true;
		for (GroupByClause clause:clauses) {
			if (first) first=false;
			else b.append(SEP);
			
			String role = clause.getRole();
			String property = clause.getProperty();
			Vertex vertex = instance.getVertex(role);
			if (property==null) {
				// append full type name
				b.append(vertex.getUserDatum("namespace"));
				b.append('.');
				b.append(vertex.getUserDatum("name"));
			}
			else {
				// append property value
				b.append(vertex.getUserDatum(property));
			}		
		}


		//System.out.println("key: " + instance + " -> " + b);
		return b.toString();
	}

}
