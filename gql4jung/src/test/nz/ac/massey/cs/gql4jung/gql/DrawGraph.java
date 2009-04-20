package test.nz.ac.massey.cs.gql4jung.gql;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;
import java.util.regex.Pattern;


import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.cluster.*;
import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.decorators.VertexStringer;

import edu.uci.ics.jung.io.GraphMLFile;

import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphLabelRenderer;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;

public class DrawGraph {

	/**
	 * @param args
	 */
		private static Graph readJungGraphFromGraphML(String graphSource) throws Exception {
		GraphMLFile input = new GraphMLFile();
		Reader reader = new FileReader(graphSource);
		Graph g = input.load(reader);
		reader.close();
		return g;
	}
	
	
	public static void main(String[] args) throws IOException, Exception {
		
		JFrame jf = new JFrame();
		//Graph g = readJungGraphFromGraphML("test_examples/packageB/clustering.graphml");
		Graph g = readJungGraphFromGraphML("xml/activation-1.1.jar.graphml");
		EdgeBetweennessClusterer clusterer = new EdgeBetweennessClusterer(0);
		for(Object o: g.getVertices()){
			Vertex v = (Vertex)o;
			String name = (String) v.getUserDatum("name");
			if(Pattern.matches(".*\\$.*",name)){
				System.out.println(name);
			}
		}
		ClusterSet set = clusterer.extract(g); 
		for(int i=0; i<set.size();i++){
			Set cluster = set.getCluster(i);
			for(Object o: cluster){
				Vertex v =  (Vertex) o;
				v.addUserDatum("cluster", "cluster-"+i, UserData.SHARED);
//				System.out.print(v.getUserDatum("name")+" ");
//				System.out.println(v.getUserDatum("cluster"));
			}
		}
		PluggableRenderer pr = new PluggableRenderer();
		VisualizationViewer vv = new VisualizationViewer(new FRLayout(g), pr);
        jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
        jf.setVisible(true);
       
        EdgeStringer stringer = new EdgeStringer(){
            public String getLabel(ArchetypeEdge e) {
                return (String) e.getUserDatum("type");
            }
        };
        pr.setEdgeStringer(stringer);
        
        VertexStringer vstringer= new VertexStringer(){

			@Override
			public String getLabel(ArchetypeVertex v) {
				return (String) v.getUserDatum("name");
			}
        };
        pr.setVertexStringer(vstringer);
        GraphMLFile gm = new GraphMLFile();
        gm.save(g, "test_examples/packageB/mygraph.graphml");
        
	}

}
