package nz.ac.massey.cs.gql4jung.browser;

import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.decorators.VertexFontFunction;
import edu.uci.ics.jung.graph.decorators.VertexIconFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.Renderer;

/**
 * Renderer (factory) for graph. 
 * @author Jens Dietrich
 */

public class GraphRenderer {
	final static int FONT_SIZE = 10;
	final static String FONT_TYPE = "Serif";
	final static Font CORE = new Font(FONT_TYPE, Font.BOLD, FONT_SIZE);
	final static Font NON_CORE = new Font(FONT_TYPE, Font.PLAIN, FONT_SIZE);
	final static Font OTHER = new Font(FONT_TYPE, Font.ITALIC, FONT_SIZE);
	
	private static PluggableRenderer renderer = null;
	
	public static Renderer getRenderer() {
		if (renderer==null) {
			renderer = new PluggableRenderer();
			PluggableRenderer pr = new PluggableRenderer();
			renderer.setVertexStringer(vertexStringer);
			renderer.setEdgeStringer(edgeStringer);
			renderer.setVertexIconFunction(vertexIconFunction);
			renderer.setVertexFontFunction(fontFunction);
		}
		return renderer;
	}
	
	private static VertexIconFunction vertexIconFunction = new VertexIconFunction() {
		Icon CLASS = getImageIcon("class.gif"); 
		Icon INTERFACE = getImageIcon("interface.gif");
		Icon CLASS_BW = getImageIcon("class-bw.gif"); 
		Icon INTERFACE_BW = getImageIcon("interface-bw.gif");
		@Override
		public Icon getIcon(ArchetypeVertex v) {
			boolean isPart = v.getUserDatum("role")!=null;
			if ("true".equals(v.getUserDatum("isInterface"))) {
				return isPart?INTERFACE:INTERFACE_BW;
			}
			if ("true".equals(v.getUserDatum("isAbstract"))) {
				return isPart?INTERFACE:INTERFACE_BW;
			}
			if ("class".equals(v.getUserDatum("type"))) {
				return isPart?CLASS:CLASS_BW;
			}
			return null;
		}
		
	};
	private static VertexStringer vertexStringer = new VertexStringer() {
		@Override
		public String getLabel(ArchetypeVertex v) {
			String role  = (String)v.getUserDatum("role");
			String namespace = (String)v.getUserDatum("namespace");
			String name = (String)v.getUserDatum("name");
			StringBuffer b = new StringBuffer();
			b.append("<html>");
			if (role!=null) {
				b.append("<it>&lt;&lt;");
				b.append(role);
				b.append("&gt;&gt;</it>");
				b.append("<br/>");
			}
			if (namespace!=null) {
				b.append(namespace);
				b.append('.');
			}
			if (name!=null) {
				b.append(name);
			}
			b.append("</html>");
			return b.toString();
		}
	}; 
	private static VertexFontFunction fontFunction = new VertexFontFunction() {
		
		@Override
		public Font getFont(Vertex v) {
			boolean isPart = v.getUserDatum("role")!=null;
			boolean isCore = "true".equals(v.getUserDatum("core"));
			if (isPart) {
				return isCore?CORE:NON_CORE;
			}
			else {
				return OTHER;
			}
		}
		
	};
	
	private static EdgeStringer edgeStringer = new EdgeStringer() {
		@Override
		public String getLabel(ArchetypeEdge e) {
			String role  = (String)e.getUserDatum("type");
			return role==null?"?":role;
		}
	}; 
	
	private static Icon getImageIcon(String string) {
		return new ImageIcon("icons/"+string);
	}
	

}
