package packageB;

import java.awt.Dimension;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseVertex;
import edu.uci.ics.jung.graph.predicates.ConnectedGraphPredicate;
import edu.uci.ics.jung.graph.predicates.GraphPredicate;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;

public class Test {

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
//		Graph g = readJungGraphFromGraphML("xml/testdata/dependency.graphml");
		Graph g = new DirectedSparseGraph();
		Vertex v1 = new DirectedSparseVertex();
		Vertex v2 = new DirectedSparseVertex();
		Vertex v3 = new DirectedSparseVertex();
		Vertex v4 = new DirectedSparseVertex();
		String name_key="name";
		String add_key="address";
		
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
        Edge e = new DirectedSparseEdge(v1, v2);
        Edge e1 = new DirectedSparseEdge(v3, v4);
        Edge e2 = new DirectedSparseEdge(v2,v3);
        g.addEdge(e);
        g.addEdge(e1);
        g.addEdge(e2);
        v1.addUserDatum(name_key, "Ali", UserData.SHARED);
        v1.addUserDatum(add_key, "New Zealand", UserData.SHARED);
		VisualizationViewer vv = new VisualizationViewer(new SpringLayout(g), new PluggableRenderer());
        jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
        jf.setVisible(true);
        for (Iterator iter = g.getEdges().iterator(); iter.hasNext();)
        {
        	Edge iedge =   (Edge) iter.next();
        	System.out.println(iedge);
        	//System.out.println(iedge.getEndpoints().equals("V1"));
        	//System.out.println(iedge.getIncidentElements().contains(v3));
        	System.out.println(iedge.getIncidentVertices().contains(v2));
        }
        System.out.println(e.getEndpoints());
       //Pair p1 = new Pair(1,2);
        
        System.out.println(g.getEdges());
        //System.out.println(e.getEndpoints());
        System.out.println(e.getOpposite(v2));
        
        System.out.println(v1.getUserDatum(name_key));
		System.out.println(v1.getUserDatum(add_key));
		//System.out.println(getIndex(v1));
		System.out.println("num of edges:"+g.numEdges()+ " num of vertices:"+g.numVertices()+ g.getVertices());
		DijkstraShortestPath alg = new DijkstraShortestPath(g);		
	}

}
