/**
 * Copyright 2010 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
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
import java.util.Stack;
import edu.uci.ics.jung.graph.*;
import nz.ac.massey.cs.gql4jung.*;

/**
 * Improved graph query engine supporting multithreading.
 * @author jens dietrich
 */
public class MultiThreadedGQLImpl extends GQLImplCore {
	private int numberOfThreads = 2;
	private int activeThreadCount = 0;
	public MultiThreadedGQLImpl() {
		super();
	}
	public MultiThreadedGQLImpl(int numberOfThreads) {
		super();
		this.numberOfThreads = numberOfThreads;
	}


	@Override
	public void query(final DirectedGraph<Vertex,Edge> graph, final Motif motif, final ResultListener listener,final boolean ignoreVariants) {
		prepareGraph(graph,motif);
		
		// initial binding bindings.gotoChildLevel();
		assert !motif.getRoles().isEmpty();
    	final String role = motif.getRoles().get(0);  
    	Collection<Vertex> vertices = graph.getVertices();
    	final int S = vertices.size();
    	final int stepSize = S<100?1:Math.round(S/100);
    	listener.progressMade(0,S);    	
    	
    	// prepare constraints
    	final List<Constraint> constraints = scheduler.getConstraints(graph, motif);
    	
    	// prepare agenda - parallelize only on top level (first role)
    	final Stack<Vertex> agenda = new Stack<Vertex>();
    	for (Vertex v:vertices) {
    		agenda.push(v); // reverses order - could use agenda.add(0, v) to retain order
    	}
    	Runnable worker = new Runnable() {
			@Override
			public void run() {
				Vertex nextNode = null;
				int counter;
				Controller controller = createController(motif,constraints,ignoreVariants);
				while (!cancel && !agenda.isEmpty()) {
					nextNode = null;
					synchronized (agenda) {
						if (!agenda.isEmpty()) {
							nextNode = agenda.pop();
						}
					}
					//Thread.yield();
					if (nextNode!=null) {
						controller.bind(role, nextNode);
						resolve(graph, motif, controller, listener);
						counter = S-agenda.size();
			    		if (counter%stepSize==0) {
			    			listener.progressMade(counter,S);
			    		}
			    		controller.reset();
					}
				}
				synchronized (MultiThreadedGQLImpl.this) {
					activeThreadCount = activeThreadCount-1;
				}
			}
    	};
    	
    	// start resolver
    	activeThreadCount = numberOfThreads;
    	for (int i=0;i<this.numberOfThreads;i++) {
    		new Thread(worker).start();
    	}
    	
    	// the main thread keeps on running so that the listener can be notified that all threads have finished
    	while (activeThreadCount>0) {
    		try {
				Thread.sleep(100); // TODO make this configurable
			} catch (InterruptedException e) {
				// TODO exception handling
				e.printStackTrace();
			}
    	}
    	listener.done();
	}


}
