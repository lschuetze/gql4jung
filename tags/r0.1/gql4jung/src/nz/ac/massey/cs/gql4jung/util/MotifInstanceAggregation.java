package nz.ac.massey.cs.gql4jung.util;


import nz.ac.massey.cs.gql4jung.MotifInstance;


/**
 * Definition of how to identify motif instances. 
 * Identification is done by computing a unique group identifier. 
 * Instances with the same identifier are considered equal. 
 * @author Jens Dietrich
 */
public interface MotifInstanceAggregation {
	public Object getGroupIdentifier (MotifInstance instance) ;
}
