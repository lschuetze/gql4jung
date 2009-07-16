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

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import org.apache.log4j.Logger;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import nz.ac.massey.cs.gql4jung.browser.PropertyBean;
import nz.ac.massey.cs.gql4jung.browser.layout.EllipticFanLayout;
import nz.ac.massey.cs.gql4jung.browser.layout.FanLayout;
import nz.ac.massey.cs.gql4jung.browser.layout.OrbitalLayout;

/**
 * Settings for the view based on jung visualisation engine.
 * @author jens dietrich
 */
public class GraphBasedQueryViewSettings implements PropertyBean {
	
	private String layout = null;
	private Font roleFont =  new Font("Monospaced", Font.PLAIN,10);
	private Font edgeFont =  new Font("Monospaced", Font.PLAIN,10);
	private Font constraintFont =  new Font("Monospaced", Font.PLAIN,10);
	private int minBoxWidth = 120;
	private int vertexTransparency = 50;
	private Color background = Color.WHITE;
	private Color roleFrameColor = Color.BLACK;
	private Color constraintFrameColor = Color.RED;
	private Color roleFillColor = Color.BLACK;
	private Color constraintFillColor = Color.RED;
	private boolean useAntiAliasing = true;
	
	public GraphBasedQueryViewSettings() {
		super();
		reset();
	}
	
	public final static GraphBasedQueryViewSettings DEFAULT_INSTANCE = load();
	public static File getStorage()  {
		return new File(".GraphBasedQueryViewSettings.xml"); 
	}

	private static String[] layouts = {
		EllipticFanLayout.class.getName(),
		OrbitalLayout.class.getName(),
		FanLayout.class.getName(),
		FRLayout.class.getName(),
		FRLayout2.class.getName(),
		SpringLayout.class.getName(),
		KKLayout.class.getName(),
		DAGLayout.class.getName()
	};
	
	public static class LayoutEditor extends ComboBoxPropertyEditor {
		public LayoutEditor() {
			super();	    
		    setAvailableValues(layouts);
		}
	}
	
	public Font getRoleFont() {
		return roleFont;
	}

	public Font getEdgeFont() {
		return edgeFont;
	}

	public Font getConstraintFont() {
		return constraintFont;
	}

	public Color getRoleFrameColor() {
		return roleFrameColor;
	}

	public Color getConstraintFrameColor() {
		return constraintFrameColor;
	}

	public Color getRoleFillColor() {
		return roleFillColor;
	}

	public Color getConstraintFillColor() {
		return constraintFillColor;
	}

	public void setRoleFont(Font roleFont) {
		this.roleFont = roleFont;
	}

	public void setEdgeFont(Font edgeFont) {
		this.edgeFont = edgeFont;
	}

	public void setConstraintFont(Font constraintFont) {
		this.constraintFont = constraintFont;
	}

	public void setRoleFrameColor(Color roleFrameColor) {
		this.roleFrameColor = roleFrameColor;
	}

	public void setConstraintFrameColor(Color constraintFrameColor) {
		this.constraintFrameColor = constraintFrameColor;
	}

	public void setRoleFillColor(Color roleFillColor) {
		this.roleFillColor = roleFillColor;
	}

	public void setConstraintFillColor(Color constraintFillColor) {
		this.constraintFillColor = constraintFillColor;
	}

	public boolean isUseAntiAliasing() {
		return useAntiAliasing;
	}

	public void setUseAntiAliasing(boolean useAntiAliasing) {
		this.useAntiAliasing = useAntiAliasing;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public int getVertexTransparency() {
		return vertexTransparency;
	}

	public void setVertexTransparency(int vertexTransparency) {
		this.vertexTransparency = vertexTransparency;
	}

	
	public int getMinBoxWidth() {
		return minBoxWidth;
	}

	public void setMinBoxWidth(int minBoxWidth) {
		this.minBoxWidth = minBoxWidth;
	}

	public String getLayout() {
		return layout;
	}
	
	Layout getLayout(Graph g) {
		try {
			Class clazz = Class.forName(layout);
			Constructor constructor = clazz.getConstructor(new Class[]{Graph.class});
			return (Layout)constructor.newInstance(new Object[]{g});
		}
		catch (Exception x){
			Logger.getLogger(this.getClass()).error("Cannot instantiate graph layout "+layout,x);
			return new EllipticFanLayout(g);
		}
	}

	private static GraphBasedQueryViewSettings load() {
		try {
			GraphBasedQueryViewSettings settings = null;
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(getStorage())));
			settings = (GraphBasedQueryViewSettings)decoder.readObject();
			decoder.close();
			Logger.getLogger(GraphBasedQueryViewSettings.class).info("Loading settings from " + getStorage());
			return settings;
		}
		catch (Exception x) {
			Logger.getLogger(GraphBasedQueryViewSettings.class).info("Cannot load settings");
		}
		return new GraphBasedQueryViewSettings();
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	@Override
	public void save() throws IOException {
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(getStorage())));
		encoder.writeObject(this);
		encoder.close();
	}

	@Override
	public void reset() {
		layout = EllipticFanLayout.class.getName();
		minBoxWidth = 120;
		vertexTransparency = 200;
		background = Color.WHITE;
		useAntiAliasing = true;
		roleFont =  new Font("Monospaced", Font.PLAIN,10);
		edgeFont =  new Font("Monospaced", Font.PLAIN,10);
		constraintFont =  new Font("Monospaced", Font.PLAIN,10);
		background = Color.WHITE;
		roleFrameColor = Color.BLACK;
		constraintFrameColor = Color.RED;
		roleFillColor = new Color(200,200,200,200);
		constraintFillColor = new Color(255,200,200,200);
		useAntiAliasing = true;
	}
	@Override
	public PropertyDescriptor[] getProperties() {
		try {
			PropertyDescriptor[] properties = {
				new PropertyDescriptor("layout",GraphBasedQueryViewSettings.class,"getLayout","setLayout"),
				new PropertyDescriptor("font for roles",GraphBasedQueryViewSettings.class,"getRoleFont","setRoleFont"),
				new PropertyDescriptor("font for constraints",GraphBasedQueryViewSettings.class,"getConstraintFont","setConstraintFont"),
				new PropertyDescriptor("font for edge labels",GraphBasedQueryViewSettings.class,"getEdgeFont","setEdgeFont"),
				new PropertyDescriptor("frame color for roles",GraphBasedQueryViewSettings.class,"getRoleFrameColor","setRoleFrameColor"),
				new PropertyDescriptor("frame color for constraints",GraphBasedQueryViewSettings.class,"getConstraintFrameColor","setConstraintFrameColor"),
				new PropertyDescriptor("fill color for roles",GraphBasedQueryViewSettings.class,"getRoleFillColor","setRoleFillColor"),
				new PropertyDescriptor("fill color for constraints",GraphBasedQueryViewSettings.class,"getConstraintFillColor","setConstraintFillColor"),
				new PropertyDescriptor("min box width",GraphBasedQueryViewSettings.class,"getMinBoxWidth","setMinBoxWidth"),
				new PropertyDescriptor("vertex transparency (alpha)",GraphBasedQueryViewSettings.class,"getVertexTransparency","setVertexTransparency"),
				new PropertyDescriptor("background colour",GraphBasedQueryViewSettings.class,"getBackground","setBackground"),
				new PropertyDescriptor("use anti aliasing (on is slower)",GraphBasedQueryViewSettings.class,"isUseAntiAliasing","setUseAntiAliasing")
			};
				
			properties[0].setPropertyEditorClass(LayoutEditor.class);			
			return properties;
		}
		catch (Exception x) {
			Logger.getLogger(this.getClass()).error("Exception initializing settings",x);
			return new PropertyDescriptor[0];
		}
	}

}
