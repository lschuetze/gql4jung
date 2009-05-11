package nz.ac.massey.cs.gql4jung.jmpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import edu.uci.ics.jung.graph.*;
import nz.ac.massey.cs.gql4jung.*;
import nz.ac.massey.cs.processors.Processor;

/**
 * Improved graph query engine.
 * @author jens dietrich
 */
public class GQLImpl extends Logging implements GQL {
	public GQLImpl() {
		super();
	}

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
	
	private boolean cancel = false;
	private ConstraintScheduler scheduler = new ConstraintSchedulerImpl();

	@Override
	public void cancel() {
		cancel = true;
	}

	@Override
	public void query(Graph graph, Motif motif, ResultListener listener) {
		
		if(motif.getGraphProcessor().size()!=0){
			for(Processor processor:motif.getGraphProcessor()){
				processor.process(graph);
			}
		}
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
    	for(Vertex v:vertices){
    		Bindings bindings = new Bindings();
    		bindings.bind(role, v);
    		counter = counter+1;
    		resolve(graph, motif, constraints, bindings, listener);
    		if (counter%stepSize==0) {
    			listener.progressMade(counter,S);
    		}
	    }
	}

	private void resolve(Graph graph, Motif motif, List<Constraint> constraints,Bindings bindings, ResultListener listener) {
		if (cancel) return;

		// check for termination
		if (constraints.isEmpty()) {
			MotifInstance instance = createInstance(graph,motif,bindings);
			listener.found(instance);
			return;
		}
		// recursion
		bindings.gotoChildLevel();  // one level down
		Constraint nextConstraint = constraints.get(0); // take the first, has been ordered by scheduler
		// new agenda 
		//List<Constraint> newAgenda = new ArrayList<Constraint>(); // no pool version
		List<Constraint> newAgenda = this.agendaPool.borrow();
		for (Constraint c:constraints) {
			if (c!=nextConstraint) newAgenda.add(c);
		} 
		
		if (LOG_GQL.isDebugEnabled()) {
			LOG_GQL.debug("recursion level "+bindings.getPosition()+", resolving: "+nextConstraint);
		}
		
		if (nextConstraint instanceof PropertyConstraint) {
			PropertyConstraint propertyConstraint = (PropertyConstraint)nextConstraint;
			String role = propertyConstraint.getOwner();
			// constraint has owner
			if (role!=null) {
				Vertex v = (Vertex)bindings.lookup(role);
				if (v==null) {
					Iterator<Vertex> iter = graph.getVertices().iterator();
					
					// the constraint has already been removed! Add it back again.
					newAgenda.add(0,propertyConstraint);
					resolveNextLevel(graph,motif,newAgenda,bindings,listener,iter,role);
				}
				else if (propertyConstraint.check(graph,v)) {
					resolve(graph,motif,newAgenda,bindings,listener);
				}
			}
			else {
				Map<String,Vertex> map = bindings.getRoleBindingsAsMap();
				// check for roles without bindings
				List<String> rolesInConstraint = propertyConstraint.getOwnerRoles();
				if (map.keySet().containsAll(rolesInConstraint)) {
					if (propertyConstraint.check(graph,map)) {
						resolve(graph,motif,newAgenda,bindings,listener);
					}	
				}
				else {
					for (String roleInConstraint:rolesInConstraint) {
						if (!map.keySet().contains(roleInConstraint)) {
							Iterator<Vertex> iter = graph.getVertices().iterator();
							// the constraint has already been removed! Add it back again.
							newAgenda.add(0,propertyConstraint);
							resolveNextLevel(graph,motif,newAgenda,bindings,listener,iter,roleInConstraint);						
						}
					}
				}
			}
		}
		else if (nextConstraint instanceof LinkConstraint) {
			LinkConstraint linkConstraint = (LinkConstraint)nextConstraint; 
			String sourceRole = linkConstraint.getSource();
			String targetRole = linkConstraint.getTarget();
			Vertex source = (Vertex)bindings.lookup(sourceRole);
			Vertex target = (Vertex)bindings.lookup(targetRole);
			if (source!=null && target!=null) {
				Object result=linkConstraint.check(graph, source, target); // path or edge
				if (result!=null) {
					bindings.bind(linkConstraint,result);
					resolve(graph,motif,newAgenda,bindings,listener);
				}
			}
			else if (source==null && target!=null) {
				Iterator<ConnectedVertex> iter =linkConstraint.getPossibleSources(graph,target);
				resolveNextLevel(graph,motif,newAgenda,bindings,listener,iter,target,sourceRole,linkConstraint);
				
			}
			else if (source!=null && target==null) {
				Iterator<ConnectedVertex> iter =linkConstraint.getPossibleTargets(graph,source);
				resolveNextLevel(graph,motif,newAgenda,bindings,listener,iter,source,targetRole,linkConstraint);
			}
			else {
				throw new IllegalStateException("cannot resolve linke constraints with two open slots");
			}
		}
		bindings.gotoParentLevel(); // one level up
		this.agendaPool.recycle(newAgenda);
	}

	private void resolveNextLevel(Graph graph, Motif motif,List<Constraint> constraints, Bindings bindings,
			ResultListener listener, Iterator<Vertex> iter, String role) {
		
		while (iter.hasNext()) {
			Vertex v = iter.next();
			bindings.bind(role,v);
			resolve(graph,motif,constraints,bindings,listener);
		}
	}

	private void resolveNextLevel(Graph graph, Motif motif, List<Constraint> constraints,Bindings bindings, ResultListener listener, 
			Iterator<ConnectedVertex> iter,Vertex end1,String end2Role,LinkConstraint linkConstraint) {
		
		while (iter.hasNext()) {
			ConnectedVertex cv = iter.next();
			Object link = cv.getLink();
			if (link instanceof Path) {
				Path path = (Path)link;
				bindings.bind(linkConstraint,path);
				if (path.getStart()==end1) {
					bindings.bind(end2Role,path.getEnd());
					resolve(graph,motif,constraints,bindings,listener);
				}
				else if (path.getEnd()==end1) {
					bindings.bind(end2Role,path.getStart());
					resolve(graph,motif,constraints,bindings,listener);
				}
			}
			else if (link instanceof Edge) {
				Edge edge = (Edge)link;
				bindings.bind(linkConstraint,edge);
				if (edge.getEndpoints().getFirst()==end1) {
					bindings.bind(end2Role,(Vertex)edge.getEndpoints().getSecond());
					resolve(graph,motif,constraints,bindings,listener);
				}
				else if (edge.getEndpoints().getSecond()==end1) {
					bindings.bind(end2Role,(Vertex)edge.getEndpoints().getFirst());
					resolve(graph,motif,constraints,bindings,listener);
				}
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
