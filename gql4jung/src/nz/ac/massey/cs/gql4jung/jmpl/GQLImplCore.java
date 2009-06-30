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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import edu.uci.ics.jung.graph.*;
import nz.ac.massey.cs.gql4jung.*;
import nz.ac.massey.cs.gql4jung.util.PathCache;

/**
 * Abstract superclass for GQL implementations. The backtracking / communication
 * with the controller is done here, the top level orchestration has to be done in subclasses.
 * @author jens dietrich
 */
public abstract class GQLImplCore extends Logging implements GQL {
	public GQLImplCore() {
		super();
	}

	protected boolean cancel = false;
	protected ConstraintScheduler scheduler = new SimpleScheduler();



	protected void resolve(DirectedGraph<Vertex,Edge> graph, Motif motif, Controller controller, ResultListener listener) {
		if (cancel) return;

		// check for termination
		if (controller.isDone()) {
			MotifInstance instance = createInstance(graph,motif,controller);
			listener.found(instance);
			return;
		}
		
		// back jumping
		if (controller.isInJumpBackMode()) {
			return;
		}
		
		// recursion
		Constraint nextConstraint = controller.next();  // one level down
		
		
		if (LOG_GQL.isDebugEnabled()) {
			LOG_GQL.debug("recursion level "+controller.getPosition()+", resolving: "+nextConstraint);
		}
		
		if (nextConstraint instanceof PropertyConstraint) {
			PropertyConstraint constraint = (PropertyConstraint)nextConstraint;
			boolean result = false;
			if (constraint.isSingleRole()) {
				Object vertexOrPath = controller.lookupAny(constraint.getFirstRole());
				if (vertexOrPath!=null) {
					result = constraint.check(vertexOrPath);
				}
				else {
					LOG_GQL.warn("encountered unresolved role "+constraint.getFirstRole()+", cannot resolve: "+constraint);
				}
			}
			else {
				Map<String,Object> bind = new HashMap<String,Object>(constraint.getRoles().size());
				for (Object role:constraint.getRoles()) {
					Object vertexOrPath = controller.lookupAny(role.toString());
					if (vertexOrPath!=null) {
						bind.put(role.toString(),vertexOrPath);
					}
					else {
						LOG_GQL.warn("encountered unresolved role "+constraint.getFirstRole()+", cannot resolve: "+constraint);
					}	
				}
				result = constraint.check(bind); 
			}
			if (result) {
				resolve(graph,motif,controller,listener);
			}

		}
		else if (nextConstraint instanceof LoopInstruction) {
			// full loop - this is the only way to progress
			LoopInstruction in = (LoopInstruction)nextConstraint; 
			String role = in.getRole();
			for (Vertex v:(Collection<Vertex>)graph.getVertices()) {
				controller.bind(role,v);
				resolve(graph,motif,controller,listener);
			}
		}
		else if (nextConstraint instanceof PathConstraint) {
			PathConstraint constraint = (PathConstraint)nextConstraint; 
			String sourceRole = constraint.getSource();
			String targetRole = constraint.getTarget();
			Vertex source = (Vertex)controller.lookupVertex(sourceRole);
			Vertex target = (Vertex)controller.lookupVertex(targetRole);
			
			if (source!=null && target!=null) {
				Iterator<Path> iter = constraint.check(graph, source, target); // path or edge
				resolveNextLevel(graph,motif,controller,listener,iter,target,sourceRole,constraint);
			}
			else if (source==null && target!=null) {
				Iterator<Path> iter = constraint.getPossibleSources(graph,target);
				resolveNextLevel(graph,motif,controller,listener,iter,target,sourceRole,constraint);
				
			}
			else if (source!=null && target==null) {
				Iterator<Path> iter =constraint.getPossibleTargets(graph,source);
				resolveNextLevel(graph,motif,controller,listener,iter,source,targetRole,constraint);
			}
			else {
				throw new IllegalStateException("cannot resolve linke constraints with two open slots");
			}
		}
		controller.backtrack(); // one level up

	}

	private void resolveNextLevel(DirectedGraph<Vertex,Edge> graph, Motif motif, Controller controller, ResultListener listener, 
			Iterator<Path> iter,Vertex end1,String end2Role,PathConstraint constraint) {
		
		while (iter.hasNext()) {
			Path path = iter.next();
			controller.bind(constraint.getRole(),path);
			if (path.getStart()==end1) {
				controller.bind(end2Role,path.getEnd());
				resolve(graph,motif,controller,listener);
			}
			else if (path.getEnd()==end1) {
				controller.bind(end2Role,path.getStart());
				resolve(graph,motif,controller,listener);
			}
		}
	}

	protected MotifInstance createInstance(Graph graph, Motif motif, Controller bindings) {
		return new MotifInstanceImpl(motif,bindings);
	}

	@Override
	public void reset() {
	}

	@Override
	public void cancel() {
		cancel = true;
		PathCache.switchCachingOff();
	}

	protected Controller createController(Motif motif,List<Constraint> constraints,boolean ignoreVariants) {
		return ignoreVariants?new BackJumpingController(motif,constraints):new Controller(motif,constraints);
	}
	
	protected void prepareGraph(DirectedGraph<Vertex,Edge> graph,Motif motif) {
		// process graph
		if(motif.getGraphProcessor().size()!=0){
			for(Processor processor:motif.getGraphProcessor()){
				processor.process(graph);
			}
		}
		// set up caching
		new LRUCache(graph,1000).install();
	}
}
