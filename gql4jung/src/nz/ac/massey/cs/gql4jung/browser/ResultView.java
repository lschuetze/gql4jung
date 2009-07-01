package nz.ac.massey.cs.gql4jung.browser;

import javax.swing.JPanel;
import edu.uci.ics.jung.graph.DirectedGraph;
import nz.ac.massey.cs.gql4jung.*;

public abstract class ResultView extends JPanel {
	public abstract String getName();
	public abstract void display(MotifInstance instance,DirectedGraph<Vertex,Edge> graph);
}
