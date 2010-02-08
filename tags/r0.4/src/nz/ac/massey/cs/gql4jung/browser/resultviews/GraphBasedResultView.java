/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.browser.resultviews;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.commons.collections15.Transformer;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.browser.PropertyBean;
import nz.ac.massey.cs.gql4jung.browser.ResultView;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GraphBasedResultView extends ResultView {
	
	private JPanel graphPane = new JPanel();
	private GraphBasedResultViewSettings settings = GraphBasedResultViewSettings.DEFAULT_INSTANCE;
	
	public GraphBasedResultView() {
		super();
		this.setLayout(new GridLayout(1,1));
		graphPane.setLayout(new GridLayout(1,1));
		this.add(new JScrollPane(graphPane));
		
	}
	
	public PropertyBean getSettings() {
		return settings;
	}

	@Override
	public void display(final MotifInstance instance,	DirectedGraph<Vertex, Edge> graph) {	
		DirectedGraph<Vertex,Edge> g = instance==null?new DirectedSparseGraph<Vertex,Edge>():this.asGraph(instance);
		//SimpleGraphView sgv = new SimpleGraphView(); //We create our graph in here
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<Vertex,Edge> layout = settings.getLayout(g);
		layout.setSize(graphPane.getSize());
		VisualizationViewer<Vertex,Edge> vv = new VisualizationViewer<Vertex,Edge>(layout);
		configureRenderer(vv.getRenderContext(),instance);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.setPreferredSize(graphPane.getSize()); //Sets the viewing area size
		vv.setBackground(settings.getBackground());
		graphPane.removeAll();
		graphPane.add(vv);
		graphPane.revalidate();
		//vv.addMouseListener(popupListener);
		// Create a graph mouse and add it to the visualization component
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(gm);
		vv.setVertexToolTipTransformer(new Transformer<Vertex,String>(){
			@Override
			public String transform(Vertex v) {
				return getToolTip(v);
			}
		});
	}
	
	private String getToolTip(Vertex v) {
		StringBuffer b = new StringBuffer();
		b.append("<html>");
		print(b,"container",v.getContainer(),false);
		print(b,"namespace",v.getNamespace(),false);
		print(b,"name",v.getName(),false);
		print(b,"type",v.getType(),false);
		print(b,"is abstract",v.isAbstract(),true);
		b.append("</html>");
		return b.toString();
	}
	private void print(StringBuffer b,String key,Object value,boolean last) {
		b.append("<i>");
		b.append(key);
		b.append("</i>:");
		b.append(value);
		if (!last) b.append("<br/>");
	}

	@Override
	public String getName() {
		return "graph view";
	}

	private void configureRenderer (RenderContext context,final MotifInstance instance) {
		
		
		final Map<Vertex,String> revMap = new HashMap<Vertex,String>();
		if (instance!=null) {
			for (String role:instance.getMotif().getRoles()) {
				revMap.put(instance.getVertex(role),role);
			}
		}
		final Map<Vertex,Color> colMap = createColorMap(instance);
		/*
		context.setEdgeDrawPaintTransformer(
			new Transformer<Edge,Paint>(){
				@Override
				public Paint transform(Edge e) {
					return new Color(0,0,0,50);
				}
			}
		);
		*/
		context.setVertexLabelTransformer(
			new Transformer<Vertex,String>(){
				@Override
				public String transform(Vertex v) {
					String role = revMap.get(v);
					StringBuffer b = new StringBuffer()
						.append("<html>");
					if (role!=null) {
						b.append("&lt;&lt;")
						.append(role==null?"?":role)
						.append("&gt;&gt")
						.append("<br/>");					
					}
					b.append(v.getName())
						.append("</html>");
					return b.toString();
				}
			}
		);
		context.setEdgeLabelTransformer(
			new Transformer<Edge,String>(){
				@Override
				public String transform(Edge e) {
					return "<<"+e.getType()+">>";
				}
			}
		);
		context.setVertexFillPaintTransformer(
			new Transformer<Vertex,Paint>() {
				@Override
				public Paint transform(Vertex v) {
					Color c = colMap.get(v);
					if (c!=null) return c;
					else return Color.white;
				}
			}
		);
		context.setVertexDrawPaintTransformer(
				new Transformer<Vertex,Paint>() {
					@Override
					public Paint transform(Vertex v) {
						return Color.black;
					}
				}
			);

		context.setVertexStrokeTransformer(
			new Transformer<Vertex, Stroke>() {
				public Stroke transform(Vertex v) {
					if (revMap.containsKey(v)) return GraphRendererConstants.STROKE_BOLD;
					else return GraphRendererConstants.STROKE_NORMAL;
				}
			}
		);	
	
		context.setVertexShapeTransformer(
				new Transformer<Vertex,Shape>() {
					@Override
					public Shape transform(Vertex v) {
						String longLabel = v.getName();
						Font f = settings.getFont4Participants();
						FontMetrics FM = GraphBasedResultView.this.getGraphics().getFontMetrics(f);
						int W = Math.max(settings.getMinBoxWidth(),FM.stringWidth(longLabel)+10);
						int H = revMap.containsKey(v)?settings.getBoxHeight4Participants():settings.getBoxHeight4NonParticipants();
						return new Rectangle(0,0,W,H);
					}
					
				}
		);
		
		context.setVertexFontTransformer(
			new Transformer<Vertex,Font>(){
				@Override
				public Font transform(Vertex v) {
					boolean hasRole = revMap.containsKey(v);
					return hasRole?settings.getFont4Participants():settings.getFont4NonParticipants();
				}
			}
		);
		context.setEdgeFontTransformer(
			new Transformer<Edge,Font>(){
				@Override
				public Font transform(Edge e) {
					return settings.getFont4Edges();
				}
			}
		);
		
	}
	
	private Map<Vertex, Color> createColorMap(MotifInstance instance) {
		if (instance==null) return new HashMap<Vertex, Color>(0);
		Set<Vertex> vertices = instance.getVertices();
		Set<String> packages = new HashSet<String>(vertices.size());
		for (Vertex v:vertices) {
			packages.add(v.getNamespace());
		}
		int count = 0;
		Map<String, Color> pmap = new HashMap<String, Color> (packages.size());
		float offset = 100/packages.size();
		offset = offset/100;
		for (String p:packages) {
			Color hsb = Color.getHSBColor(count*offset,settings.getVertexSaturation(),settings.getVertexBrightness());
			pmap.put(p,new Color(hsb.getRed(),hsb.getGreen(),hsb.getBlue(),settings.getVertexTransparency())); // transparency
			//pmap.put(p,hsb);
			count = count+1;
		}
		Map<Vertex, Color> map = new HashMap<Vertex, Color> (packages.size());
		for (Vertex v:vertices) {
			map.put(v,pmap.get(v.getNamespace()));
		}
		return map;
	}

	private DirectedGraph asGraph(MotifInstance instance) {
		DirectedGraph<Vertex,Edge> g = new DirectedSparseGraph<Vertex,Edge>();
		Motif motif = instance.getMotif();
		// vertices
		Set<Vertex> vertices = new HashSet<Vertex>();
		for (String role:motif.getRoles()) {
			Vertex v = instance.getVertex(role);
			if (v==null) {
				g.addVertex(v);
				vertices.add(v);
			}
		};
		// edges
		for (String role:motif.getPathRoles()) {
			Path p = instance.getPath(role);
			if (p!=null) {
				for (Edge e:p.getEdges()) {
					Vertex v1 = e.getStart();
					Vertex v2 = e.getEnd();
					if (!vertices.contains(v1)) vertices.add(v1);
					if (!vertices.contains(v2)) vertices.add(v2);
					g.addEdge(e,v1,v2);
				}
			}
		}
		return g;
	}
	
}