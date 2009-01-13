/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gpl4jung.constraints;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.Predicate;

import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.ConnectedVertex;
import nz.ac.massey.cs.gpl4jung.Path;
import edu.uci.ics.jung.algorithms.connectivity.BFSDistanceLabeler;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.utils.GraphProperties;
/**
 * Constraint to check the existence of paths between nodes.
 * @author jens.dietrich@gmail.com
 *
 */
public class PathConstraint extends LinkConstraint<Path> {
	private int minLength = 1;
	private int maxLength = -1; // this means unbound
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	public int getMinLength() {
		return minLength;
	}
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}
	
	public Iterator<ConnectedVertex<Path>> getPossibleSources(Graph g,Vertex target) {
		BFSDistanceLabeler bdl = new BFSDistanceLabeler();
		bdl.labelDistances(g, target);
		Set mPred = new HashSet();
		StringLabeller sl = StringLabeller.getLabeller(g);
		// grab a predecessor
		Vertex v = target;
		Set prd = bdl.getPredecessors(v);
		mPred.add( target );
		while( prd != null && prd.size() > 0) {
			System.out.print("Preds of " + sl.getLabel(v) + " are: ");
			for (Iterator iter = prd.iterator(); iter.hasNext();) {
				Vertex x = (Vertex) iter.next();
				System.out.print( sl.getLabel(x) +" " );
			}
			System.out.println();
			v = (Vertex) prd.iterator().next();
			mPred.add( v );
			if ( v == target ) 
			prd = bdl.getPredecessors(v);
		
		}
		return null;
		//		final Collection<Vertex> nodes= g.getVertices();
//		final Iterator<Vertex> vItr = nodes.iterator();
//		Predicate filter = new PredicateDecorator();
//	
//		/*boolean evaluate(Object obj)
//		{
//			Vertex x = (Vertex)x;
//			return GraphProperties.isConnected(g);
//		}*/
//		return Iterator.filter(vItr, filter);
//		
//		//return null; // TODO
	}
	public Iterator<ConnectedVertex<Path>>  getPossibleTargets(Graph g,Vertex source){
		return null; // TODO
	}
	public Path check(Graph g,Vertex source,Vertex target){
		BFSDistanceLabeler bdl = new BFSDistanceLabeler();
		bdl.labelDistances(g, source);
		Set mPred = new HashSet();
		
		StringLabeller sl = StringLabeller.getLabeller(g);
		
		// grab a predecessor
		Vertex v = target;
		Set prd = bdl.getPredecessors(v);
		mPred.add( target );
		while( prd != null && prd.size() > 0) {
			System.out.print("Preds of " + sl.getLabel(v) + " are: ");
			for (Iterator iter = prd.iterator(); iter.hasNext();) {
				Vertex x = (Vertex) iter.next();
				System.out.print( sl.getLabel(x) +" " );
			}
			System.out.println();
			v = (Vertex) prd.iterator().next();
			mPred.add( v );
			if ( v == source );
			prd = bdl.getPredecessors(v);
		}
		
		return null; // TODO
	}
	
}
