/**
 * 
 */
package nz.ac.massey.cs.processors;

import edu.uci.ics.jung.graph.Graph;

/**
 * Interface to process clusters in graphs. 
 * @author Ali
 */
public interface Processor {
	Graph process(Graph g);
}
