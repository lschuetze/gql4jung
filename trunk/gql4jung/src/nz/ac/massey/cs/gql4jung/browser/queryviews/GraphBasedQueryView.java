/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.browser.queryviews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import nz.ac.massey.cs.gql4jung.Constraint;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.PathConstraint;
import nz.ac.massey.cs.gql4jung.PropertyConstraint;
import nz.ac.massey.cs.gql4jung.browser.PropertyBean;
import nz.ac.massey.cs.gql4jung.browser.QueryView;
import nz.ac.massey.cs.gql4jung.browser.RankedVertex;
import nz.ac.massey.cs.gql4jung.xml.XMLMotifReader;


/**
 * Viewer for queries.
 * @author Jens Dietrich
 */
public class GraphBasedQueryView extends QueryView {
	
	final static Stroke EDGE_STROKE = new BasicStroke(1.0f);
	final static Stroke PATH_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 10.0f, new float[]{ 2.0f }, 0.0f);
	final static Stroke CONSTRAINT_STROKE = new BasicStroke(0.5f);
	final static Stroke TYPE_VERTEX_STROKE = new BasicStroke(2);
	final static Stroke CONSTRAINT_VERTEX_STROKE = new BasicStroke(0.2f);
	
	abstract class VisualVertex implements RankedVertex {
	}
	class VisualEdge {

	}
	class TypeVertex extends VisualVertex {
		String role = null;
		@Override
		public int getDegree() {
			return 0;
		}
	}
	class ConstraintVertex extends VisualVertex {
		String constraint = null;
		@Override
		public int getDegree() {
			return 1;
		}
	}
	class DepEdge extends VisualEdge {
		String role = null;
		int minLength = -1;
		int maxLength = -1;
	}
	class ConstraintEdge extends VisualEdge {
		
	}
	
	private Motif model = null;
	private JPanel graphPane = new JPanel(new GridLayout(1,1));
	private GraphBasedQueryViewSettings settings = GraphBasedQueryViewSettings.DEFAULT_INSTANCE;
	public Motif getModel() {
		return model;
	}


	public void display(Motif model,String source)  {
		this.model = model;
		DirectedGraph<VisualVertex,VisualEdge> g = asGraph(model); 
		Layout layout = settings.getLayout(g);
		layout.setSize(graphPane.getSize());
		VisualizationViewer<VisualVertex,VisualEdge> vv = new VisualizationViewer<VisualVertex,VisualEdge>(layout);
		configureRenderer(vv.getRenderContext());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.setPreferredSize(graphPane.getSize()); //Sets the viewing area size
		vv.setBackground(settings.getBackground());
		
		graphPane.removeAll();
		graphPane.add(vv);
		graphPane.revalidate();
		
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(gm);
		/*
		vv.setVertexToolTipTransformer(new Transformer<VisualVertex,String>(){
			@Override
			public String transform(VisualVertex v) {
				return v.role;
			}
		});
		*/
		
	}

	private void configureRenderer(RenderContext<nz.ac.massey.cs.gql4jung.browser.queryviews.GraphBasedQueryView.VisualVertex, nz.ac.massey.cs.gql4jung.browser.queryviews.GraphBasedQueryView.VisualEdge> context) {
		context.setVertexLabelTransformer(
			new Transformer<VisualVertex,String>(){
				@Override
				public String transform(VisualVertex v) {
					if (v instanceof TypeVertex) {
						TypeVertex tv = (TypeVertex)v;
						StringBuffer b = new StringBuffer()
							.append("<html>")
							.append(tv.role)				
							.append("</html>");
						return b.toString();
					}
					else if (v instanceof ConstraintVertex) {
						ConstraintVertex cv = (ConstraintVertex)v;
						return cv.constraint;
					}
					else return "";
				}
			}
		);
		context.setEdgeLabelTransformer(
			new Transformer<VisualEdge,String>(){
				@Override
				public String transform(VisualEdge e) {					
					if (e instanceof DepEdge) {
						DepEdge de = (DepEdge)e;
						StringBuffer b = new StringBuffer();
						if (de.minLength==de.maxLength) {
							b.append(de.maxLength);
						}
						else {
							b.append(de.minLength)
							.append("-")
							.append(de.maxLength==-1?"many":de.maxLength);
						}
						return b.toString();
					}
					else return null;
				}
			}
		);

		context.setEdgeStrokeTransformer(new Transformer<VisualEdge,Stroke>() {
				@Override
				public Stroke transform(nz.ac.massey.cs.gql4jung.browser.queryviews.GraphBasedQueryView.VisualEdge e) {		
					if (e instanceof DepEdge) {
						DepEdge de = (DepEdge)e;
						return de.maxLength==1?EDGE_STROKE:PATH_STROKE;
					}
					else if (e instanceof ConstraintEdge) {
						return CONSTRAINT_STROKE;
					}
					else return EDGE_STROKE;
				}
			}
		);
		context.setVertexStrokeTransformer(
			new Transformer<VisualVertex,Stroke>() {
				@Override
				public Stroke transform(nz.ac.massey.cs.gql4jung.browser.queryviews.GraphBasedQueryView.VisualVertex v) {		
					return v instanceof TypeVertex?TYPE_VERTEX_STROKE:CONSTRAINT_VERTEX_STROKE;
				}
			}
		);
		context.setVertexShapeTransformer(
			new Transformer<VisualVertex,Shape>() {
				@Override
				public Shape transform(VisualVertex v) {
					Font f = v instanceof TypeVertex?settings.getRoleFont():settings.getConstraintFont();
					FontMetrics FM = GraphBasedQueryView.this.getGraphics().getFontMetrics(f);
					int W = Math.max(settings.getMinBoxWidth(),FM.stringWidth(getLongLabel(v))+10);
					int H = v instanceof TypeVertex?30:22;
					return new Rectangle2D.Float(-W/2,-H/2,W,H);
				}
				
			}
		);
		context.setVertexFillPaintTransformer(
			new Transformer<VisualVertex,Paint>() {
				@Override
				public Paint transform(VisualVertex v) {
					return v instanceof TypeVertex?makeTransparent(settings.getRoleFillColor()):makeTransparent(settings.getConstraintFillColor());
				}
			}
		);
		context.setVertexDrawPaintTransformer(
				new Transformer<VisualVertex,Paint>() {
					@Override
					public Paint transform(VisualVertex v) {
						return v instanceof TypeVertex?settings.getRoleFrameColor():settings.getConstraintFrameColor();
					}
				}				
		);
		context.setEdgeDrawPaintTransformer(
				new Transformer<VisualEdge,Paint>() {
					@Override
					public Paint transform(VisualEdge v) {
						return v instanceof DepEdge?settings.getRoleFrameColor():settings.getConstraintFrameColor();
					}
				}				
		);
		context.setVertexFontTransformer(
			new Transformer<VisualVertex,Font>(){
				@Override
				public Font transform(VisualVertex v) {
					return v instanceof TypeVertex?settings.getRoleFont():settings.getConstraintFont();
				}
			}
		);
		context.setEdgeFontTransformer(
			new Transformer<VisualEdge,Font>(){
				@Override
				public Font transform(VisualEdge e) {
					return settings.getEdgeFont();
				}
			}
		);
		context.setArrowDrawPaintTransformer(
			new Transformer<VisualEdge,Paint>() {
				@Override
				public Paint transform(VisualEdge v) {
					return v instanceof DepEdge?settings.getRoleFrameColor():makeTransparent(settings.getBackground(),255);
				}
			}
		);
		context.setArrowFillPaintTransformer(
				new Transformer<VisualEdge,Paint>() {
					@Override
					public Paint transform(VisualEdge v) {
						return v instanceof DepEdge?settings.getRoleFrameColor():makeTransparent(settings.getBackground(),255);
					}
				}
			);
	}
	
	private String getLongLabel (VisualVertex v) {
		if (v instanceof TypeVertex) {
			return ((TypeVertex)v).role;
		}
		else if (v instanceof ConstraintVertex) {
			return ((ConstraintVertex)v).constraint;
		}
		else return "";
	}

	public GraphBasedQueryView() {
		super();
		initialize();
	}

	public static void show(JFrame parent,Motif motif,String title) {
		JDialog dlg = new JDialog(parent,title,false);
		GraphBasedQueryView qv = new GraphBasedQueryView();
		qv.display(motif,null);
		dlg.add(qv);
		dlg.setTitle(title);
		dlg.setSize(900,600);
		dlg.setLocation(100,100);
		dlg.setVisible(true);		
	}
	public static void main(String[] args) throws Exception {
		String query = "cd.xml";
		InputStream in = new FileInputStream("queries/"+query);
		Motif motif = new XMLMotifReader().read(in);
		in.close();
		show(null,motif,"Query Viewer");
	}
	
	private void initialize() {
		this.setLayout(new GridLayout(1,1));
		graphPane.setLayout(new GridLayout(1,1));
		this.add(new JScrollPane(graphPane));
		
	}
	
	private DirectedGraph<VisualVertex,VisualEdge> asGraph(Motif motif) {
		DirectedGraph<VisualVertex,VisualEdge> g = new DirectedSparseGraph<VisualVertex,VisualEdge>();
		Map<String,TypeVertex> verticesByRole = new HashMap<String,TypeVertex>();
		for (String r:model.getRoles()) {
			TypeVertex v = new TypeVertex();
			v.role = r;
			g.addVertex(v);
			verticesByRole.put(r,v);
		}
		for (Constraint c:model.getConstraints()) {
			if (c instanceof PathConstraint) {
				PathConstraint pc = (PathConstraint)c;
				DepEdge e = new DepEdge();
				e.role = pc.getRole();
				e.maxLength = pc.getMaxLength();
				e.minLength = pc.getMinLength();
				TypeVertex v1 = verticesByRole.get(pc.getSource());
				TypeVertex v2 = verticesByRole.get(pc.getTarget());
				g.addEdge(e, v1, v2);
			}
			if (c instanceof PropertyConstraint) {
				PropertyConstraint pc = (PropertyConstraint)c;
				ConstraintVertex cv = new ConstraintVertex();
				cv.constraint = pc.getExpression();
				g.addVertex(cv);
				List<TypeVertex> participants = new ArrayList<TypeVertex>();
				for (String role:pc.getRoles()) {
					TypeVertex v = verticesByRole.get(role);
					if (v!=null) participants.add(v);
				}
				// otherwise, referenced roles might be path roles - TODO
				if (participants.size()==pc.getRoles().size()) {
					for (TypeVertex v:participants) {
						ConstraintEdge e = new ConstraintEdge();
						g.addEdge(e,v,cv);
					}
				}
			}
		}
		return g;
	}
	
	public String getName() {
		return "motif as graph";
	}
	public PropertyBean getSettings() {
		return settings;
	}
	private Color makeTransparent(Color c) {
		return makeTransparent(c,settings.getVertexTransparency());
	} 
	private Color makeTransparent(Color c, int alpha) {
		return new Color(c.getRed(),c.getGreen(),c.getBlue(),alpha);
	} 
	
}
