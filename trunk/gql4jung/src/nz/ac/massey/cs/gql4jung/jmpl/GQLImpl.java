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
 * Improved graph query engine.
 * @author jens dietrich
 */
public class GQLImpl extends Logging implements GQL {
	public GQLImpl() {
		super();
	}

	/*
	private ObjectPool<List<Constraint>> agendaPool = new ObjectPool<List<Constraint>>(100) {

		@Override
		public List<Constraint> createNew() {
			return new ArrayList<Constraint>();
		}
		@Override
		public void reset(List<Constraint> l) {
			l.clear();
		}		
	} ;
	*/
	
	private boolean cancel = false;
	private ConstraintScheduler scheduler = new SimpleScheduler();

	@Override
	public void cancel() {
		cancel = true;
		PathCache.switchCachingOff();
	}

	@Override
	public void query(DirectedGraph<Vertex,Edge> graph, Motif motif, ResultListener listener) {
		// process graph
		if(motif.getGraphProcessor().size()!=0){
			for(Processor processor:motif.getGraphProcessor()){
				processor.process(graph);
			}
		}
		// set up caching
		new LRUCache(graph,1000).install();
		
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
    	Agenda agenda = new Agenda(constraints);
    	
    	// start resolver
    	for(Vertex v:vertices){
    		Bindings bindings = new Bindings();
    		bindings.bind(role, v);
    		counter = counter+1;
    		resolve(graph, motif, agenda, bindings, listener);
    		if (counter%stepSize==0) {
    			listener.progressMade(counter,S);
    		}
	    }
    	// reset caching
    	PathCache.switchCachingOff();
	}

	private void resolve(DirectedGraph<Vertex,Edge> graph, Motif motif, Agenda agenda,Bindings bindings, ResultListener listener) {
		if (cancel) return;

		// check for termination
		if (agenda.isDone()) {
			MotifInstance instance = createInstance(graph,motif,bindings);
			listener.found(instance);
			return;
		}
		// recursion
		bindings.gotoNextLevel();  // one level down
		Constraint nextConstraint = agenda.next(); // take the first, has been ordered by scheduler
		
		if (LOG_GQL.isDebugEnabled()) {
			LOG_GQL.debug("recursion level "+bindings.getPosition()+", resolving: "+nextConstraint);
		}
		
		if (nextConstraint instanceof PropertyConstraint) {
			PropertyConstraint constraint = (PropertyConstraint)nextConstraint;
			boolean result = false;
			if (constraint.isSingleRole()) {
				Vertex v = (Vertex)bindings.lookup(constraint.getFirstRole());
				if (v!=null) {
					result = constraint.check(v);
				}
				else {
					LOG_GQL.warn("encountered unresolved role "+constraint.getFirstRole()+", cannot resolve: "+constraint);
				}
			}
			else {
				Map<String,GraphElement> bind = new HashMap<String,GraphElement>();
				for (Object role:constraint.getRoles()) {
					Vertex v = (Vertex)bindings.lookup(role.toString());
					if (v!=null) {
						bind.put(role.toString(),v);
					}
					else {
						LOG_GQL.warn("encountered unresolved role "+constraint.getFirstRole()+", cannot resolve: "+constraint);
					}	
				}
				result = constraint.check(bind); 
			}
			if (result) {
				resolve(graph,motif,agenda,bindings,listener);
			}

		}
		else if (nextConstraint instanceof LoopInstruction) {
			// full loop - this is the only way to progress
			LoopInstruction in = (LoopInstruction)nextConstraint; 
			String role = in.getRole();
			for (Vertex v:(Collection<Vertex>)graph.getVertices()) {
				bindings.bind(role,v);
				resolve(graph,motif,agenda,bindings,listener);
			}
		}
		else if (nextConstraint instanceof PathConstraint) {
			PathConstraint constraint = (PathConstraint)nextConstraint; 
			String sourceRole = constraint.getSource();
			String targetRole = constraint.getTarget();
			Vertex source = (Vertex)bindings.lookup(sourceRole);
			Vertex target = (Vertex)bindings.lookup(targetRole);
			if (source!=null && target!=null) {
				Path result=constraint.check(graph, source, target); // path or edge
				if (result!=null) {
					bindings.bind(constraint.getRole(),result);
					resolve(graph,motif,agenda,bindings,listener);
				}
			}
			else if (source==null && target!=null) {
				Iterator<Path> iter =constraint.getPossibleSources(graph,target);
				resolveNextLevel(graph,motif,agenda,bindings,listener,iter,target,sourceRole,constraint);
				
			}
			else if (source!=null && target==null) {
				Iterator<Path> iter =constraint.getPossibleTargets(graph,source);
				resolveNextLevel(graph,motif,agenda,bindings,listener,iter,source,targetRole,constraint);
			}
			else {
				throw new IllegalStateException("cannot resolve linke constraints with two open slots");
			}
		}
		bindings.backtrack(); // one level up
		agenda.backtrack();
	}

	private void resolveNextLevel(DirectedGraph<Vertex,Edge> graph, Motif motif, Agenda agenda,Bindings bindings, ResultListener listener, 
			Iterator<Path> iter,Vertex end1,String end2Role,PathConstraint constraint) {
		
		while (iter.hasNext()) {
			Path path = iter.next();
			bindings.bind(constraint.getRole(),path);
			if (path.getStart()==end1) {
				bindings.bind(end2Role,path.getEnd());
				resolve(graph,motif,agenda,bindings,listener);
			}
			else if (path.getEnd()==end1) {
				bindings.bind(end2Role,path.getStart());
				resolve(graph,motif,agenda,bindings,listener);
			}
		}
	}

	private MotifInstance createInstance(Graph graph, Motif motif, Bindings bindings) {
		//System.out.println("creating result");
		return new MotifInstanceImpl(motif,bindings);
	}

	@Override
	public void reset() {


	}

}
