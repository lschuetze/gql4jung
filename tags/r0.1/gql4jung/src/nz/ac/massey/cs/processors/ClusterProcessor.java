package nz.ac.massey.cs.processors;

import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserData;

/**
 * Implementation class for processing clusters in graph.
 * @author Ali
 *
 */
public class ClusterProcessor implements Processor {
	
	String processorClass; 
	
	@Override
	public Graph process(Graph g) {
		EdgeBetweennessClusterer clusterer = new EdgeBetweennessClusterer(0);
		ClusterSet cset = clusterer.extract(g); 
		for(int i=0; i<cset.size();i++){
			String l = "cluster-"+i;
			Set cluster = cset.getCluster(i);
			for(Object o: cluster){
				Vertex v =  (Vertex) o;
				v.addUserDatum("cluster",l, UserData.SHARED);
			}
		}
		return g;
	}
	public String getProcessorClass() {
		return processorClass;
	}
	public void setProcessorClass(String processorClass) {
		this.processorClass = processorClass;
	}
}
