package nz.ac.massey.cs.gql4jung.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nz.ac.massey.cs.gql4jung.GroupByClause;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Vertex;

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
		List identifier = new ArrayList();
		for (GroupByClause c:clauses) {
			String role = c.getRole();
			Vertex v = instance.getVertex(role);
			Object value = c.getGroup(v);
			if (clauses.size()==1) return value;
			else identifier.add(value);
		}

		return identifier;
	}

}
