package nz.ac.massey.cs.gql4jung.browser;


import nz.ac.massey.cs.gpl4jung.MotifInstance;


/**
 * Definition of how to group vertices. 
 * Groups are disjoint, and each vertex is in one group.
 * Groups are defined by using key objects which defined groups.
 * This makes it possible to organise groups in HashMaps.
 * @author Jens Dietrich
 */
public interface VertexGroupByDefinition {

	public Object getGroupIdentifier (MotifInstance instance) ;
}
