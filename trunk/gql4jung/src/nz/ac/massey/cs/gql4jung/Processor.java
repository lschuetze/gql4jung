package nz.ac.massey.cs.gql4jung;

import edu.uci.ics.jung.graph.Graph;

/**
 * Interface to pre-process a graph before querying. 
 * This annotates the graph, it does not create a new graph.
 * @author jens dietrich
 */
public interface Processor {
	void process(Graph<Vertex,Edge> g);
}
