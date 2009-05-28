package nz.ac.massey.cs.gql4jung;
/**
 * Group by clause, based on an expression.
 * @author jens dietrich
 */
public interface GroupByClause {
	/**
	 * Get the role.
	 * @return
	 */
	public String getRole() ;
	/**
	 * Compute the group for a given vertex.
	 * @param o
	 * @return
	 */
	public Object getGroup(Vertex v);
}
