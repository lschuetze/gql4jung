package nz.ac.massey.cs.processors;

import java.util.Set;

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Processor;
import nz.ac.massey.cs.gql4jung.Vertex;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.Graph;

/**
 * Implementation class for processing clusters in graph.
 * @author jens dietrich
 */
public class Clusterer implements Processor {
	
	@Override
	public void process(Graph<Vertex, Edge> g){
		EdgeBetweennessClusterer clusterer = new EdgeBetweennessClusterer(0);
		Set<Set<Vertex>> clusters = clusterer.transform(g);  
		int counter = 1;
		for (Set<Vertex> cluster:clusters) {
			String label = "cluster-"+counter;
			counter=counter+1;
			for (Vertex v:cluster) {
				v.setCluster(label);
			}
		}
	}


}
