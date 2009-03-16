package test.nz.ac.massey.cs.gpl4jung.gql;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


import javax.swing.JFrame;

import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
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
		Graph g = readJungGraphFromGraphML("test_examples/separation.graphml");

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
        
	}

}
