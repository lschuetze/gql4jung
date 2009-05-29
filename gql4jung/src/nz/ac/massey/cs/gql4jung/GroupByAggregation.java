/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
