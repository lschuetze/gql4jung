package nz.ac.massey.cs.gql4jung.constraints;

import java.util.*;
import com.google.common.base.Predicate;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

public class NodeIterator implements Iterable<List<Edge>> {
	
	private Vertex start = null;
	private int minLength = 1;
	private int maxLength = -1;
	private boolean outgoing = true;  // false means incoming paths
	private Predicate<Edge> filter = null; 
	
	public NodeIterator(Vertex start, int minLength, int maxLength, boolean outgoing, Predicate<Edge> filter) {
		super();
		this.filter = filter;
		this.maxLength = maxLength;
		this.minLength = minLength;
		this.outgoing = outgoing;
		this.start = start;
	}

	

	public Iterator<List<Edge>> iterator() {
		// we pre init the iterator, this should be done on demand
		Collection<List<Edge>> coll = new ArrayList<List<Edge>> ();
		Map<Vertex,Object> visited = new IdentityHashMap<Vertex,Object>();
		Collection<List<Edge>> layer = new ArrayList<List<Edge>>();
		
		layer.add(new ArrayList<Edge>());
		if (this.minLength==0) {
			coll.add(new ArrayList<Edge>());
			visited.put(start,null);
		}
		
		collectPaths(1,coll,visited,layer);
		return coll.iterator();
		
	}
	private void collectPaths(int i, Collection<List<Edge>> coll,Map<Vertex, Object> visited, Collection<List<Edge>> lastLayer) {
		if (maxLength!=-1 && i>=maxLength) return ; // do not continue
		Collection<List<Edge>> nextLayer = new ArrayList<List<Edge>>();
		boolean hasNewVertices = false;
		for (List<Edge> edges:lastLayer) {
			Vertex end = this.getEnd(edges);
			Collection<Edge> edges1 = this.getNext(end);
			for (Edge edge:edges1) {
				if (filter==null || filter.apply(edge)) {
					Vertex newEnd = this.getEndPoint(edge);
					if (!visited.containsKey(newEnd)) {
						// new node, not yet seen
						hasNewVertices = true;
						visited.put(newEnd, null);
						// if min length is ok, add this
						List<Edge> newList = new ArrayList<Edge>();
						if (!this.outgoing) newList.add(edge);
						newList.addAll(edges);
						if (this.outgoing) newList.add(edge);						
						nextLayer.add(newList);
						if (i>=minLength) {
							coll.add(newList);
						}
					}
				}
			}
		}
		if (hasNewVertices) {
			this.collectPaths(i+1, coll, visited, nextLayer);
		}

	}
	private Vertex getEndPoint(Edge edge) {
		if (this.outgoing) return (Vertex) edge.getEndpoints().getSecond();
		else return (Vertex) edge.getEndpoints().getFirst();
	}
	private Collection<Edge> getNext(Vertex v) {
		return this.outgoing?v.getOutEdges():v.getInEdges();
	}
	private Vertex getEnd(List<Edge> edges) {
		if (edges.size()==0) return start;
		else if (this.outgoing) return (Vertex) edges.get(edges.size()-1).getEndpoints().getSecond();
		else return (Vertex) edges.get(0).getEndpoints().getFirst();
	}

}
