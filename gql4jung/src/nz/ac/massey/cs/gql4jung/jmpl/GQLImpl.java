/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.jmpl;

import java.util.Collection;
import java.util.List;
import edu.uci.ics.jung.graph.*;
import nz.ac.massey.cs.gql4jung.*;
import nz.ac.massey.cs.gql4jung.util.PathCache;

/**
 * Improved graph query engine.
 * @author jens dietrich
 */
public class GQLImpl extends GQLImplCore {
	public GQLImpl() {
		super();
	}


	@Override
	public void query(DirectedGraph<Vertex,Edge> graph, Motif motif, ResultListener listener,boolean ignoreVariants) {
		prepareGraph(graph,motif);
		
		// initial binding bindings.gotoChildLevel();
		assert !motif.getRoles().isEmpty();
    	String role = motif.getRoles().get(0);  
    	Collection<Vertex> vertices = graph.getVertices();
    	int S = vertices.size();
    	int counter = 0;
    	int stepSize = S<100?1:Math.round(S/100);
    	listener.progressMade(0,S);
    	
    	// prepare constraints
    	List<Constraint> constraints = scheduler.getConstraints(graph, motif);
    	
    	// start resolver
    	Controller controller = createController(motif,constraints,ignoreVariants);
    	
    	for(Vertex v:vertices){
    		controller.bind(role, v);
    		counter = counter+1;
    		resolve(graph, motif, controller, listener);
    		if (counter%stepSize==0) {
    			listener.progressMade(counter,S);
    		}
    		controller.reset();
	    }
    	// reset caching
    	PathCache.switchCachingOff();
	}


}
