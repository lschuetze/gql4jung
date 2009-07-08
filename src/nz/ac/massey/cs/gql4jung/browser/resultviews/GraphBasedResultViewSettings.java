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
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import nz.ac.massey.cs.gql4jung.browser.PropertyBean;
import nz.ac.massey.cs.gql4jung.browser.layout.EllipticFanLayout;
import nz.ac.massey.cs.gql4jung.browser.layout.FanLayout;

/**
 * Settings for the view based on jung visualisation engine.
 * @author jens dietrich
 */
public class GraphBasedResultViewSettings implements PropertyBean {
	public GraphBasedResultViewSettings() {
		super();
		reset();
	}
	
	public final static GraphBasedResultViewSettings DEFAULT_INSTANCE = load();
	public static File getStorage()  {
		return new File(".GraphBasedResultViewSettings.xml"); 
	}

	private static String[] layouts = {
		EllipticFanLayout.class.getName(),
		FanLayout.class.getName(),
		FRLayout.class.getName(),
		SpringLayout.class.getName(),
		KKLayout.class.getName()
	};
	
	public static class LayoutEditor extends ComboBoxPropertyEditor {
		public LayoutEditor() {
			super();	    
		    setAvailableValues(layouts);
		}
	}
	
	private String layout = null;
	private Font font4participants =  new Font("Monospaced", Font.PLAIN,10);
	private Font font4nonparticipants =  new Font("Monospaced", Font.ITALIC,10);
	private Font font4edges =  new Font("Monospaced", Font.PLAIN,10);
	private int minBoxWidth = 120;
	private int boxHeight4participants = 36;
	private int boxHeight4nonparticipants = 24;
	private int vertexTransparency = 50;
	private float vertexSaturation = (float) 0.8;
	private float vertexBrightness = (float) 0.8;
	private Color background = Color.WHITE;
	
	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public int getVertexTransparency() {
		return vertexTransparency;
	}

	public float getVertexSaturation() {
		return vertexSaturation;
	}

	public float getVertexBrightness() {
		return vertexBrightness;
	}

	public void setVertexTransparency(int vertexTransparency) {
		this.vertexTransparency = vertexTransparency;
	}

	public void setVertexSaturation(float vertexSaturation) {
		this.vertexSaturation = vertexSaturation%256;
	}

	public void setVertexBrightness(float vertexBrightness) {
		this.vertexBrightness = vertexBrightness%256;
	}
	
	public int getMinBoxWidth() {
		return minBoxWidth;
	}

	public int getBoxHeight4Participants() {
		return boxHeight4participants;
	}

	public int getBoxHeight4NonParticipants() {
		return boxHeight4nonparticipants;
	}

	public void setMinBoxWidth(int minBoxWidth) {
		this.minBoxWidth = minBoxWidth;
	}

	public void setBoxHeight4Participants(int boxHeight4participants) {
		this.boxHeight4participants = boxHeight4participants;
	}

	public void setBoxHeight4NonParticipants(int boxHeight4nonparticipants) {
		this.boxHeight4nonparticipants = boxHeight4nonparticipants;
	}

	
	public Font getFont4Edges() {
		return font4edges;
	}

	public void setFont4Edges(Font font4edges) {
		this.font4edges = font4edges;
	}

	public Font getFont4Participants() {
		return font4participants;
	}

	public Font getFont4NonParticipants() {
		return font4nonparticipants;
	}

	public void setFont4Participants(Font font4participants) {
		this.font4participants = font4participants;
	}

	public void setFont4NonParticipants(Font font4nonparticipants) {
		this.font4nonparticipants = font4nonparticipants;
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

	private static GraphBasedResultViewSettings load() {
		try {
			GraphBasedResultViewSettings settings = null;
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(getStorage())));
			settings = (GraphBasedResultViewSettings)decoder.readObject();
			decoder.close();
			Logger.getLogger(GraphBasedResultViewSettings.class).info("Loading settings from " + getStorage());
			return settings;
		}
		catch (Exception x) {
			Logger.getLogger(GraphBasedResultViewSettings.class).info("Cannot load settings");
		}
		return new GraphBasedResultViewSettings();
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	@Override
	public PropertyDescriptor[] getProperties() {
		try {
			PropertyDescriptor[] properties = {
				new PropertyDescriptor("layout",GraphBasedResultViewSettings.class,"getLayout","setLayout"),
				new PropertyDescriptor("font for participant labels",GraphBasedResultViewSettings.class,"getFont4Participants","setFont4Participants"),
				new PropertyDescriptor("font for non participant labels",GraphBasedResultViewSettings.class,"getFont4NonParticipants","setFont4NonParticipants"),
				new PropertyDescriptor("font for edge labels",GraphBasedResultViewSettings.class,"getFont4Edges","setFont4Edges"),				
				new PropertyDescriptor("box height for participant nodes",GraphBasedResultViewSettings.class,"getBoxHeight4Participants","setBoxHeight4Participants"),
				new PropertyDescriptor("box height for non participant nodes",GraphBasedResultViewSettings.class,"getBoxHeight4NonParticipants","setBoxHeight4NonParticipants"),
				new PropertyDescriptor("min box width",GraphBasedResultViewSettings.class,"getMinBoxWidth","setMinBoxWidth"),
				new PropertyDescriptor("vertex saturation",GraphBasedResultViewSettings.class,"getVertexSaturation","setVertexSaturation"),
				new PropertyDescriptor("vertex brightness",GraphBasedResultViewSettings.class,"getVertexBrightness","setVertexBrightness"),
				new PropertyDescriptor("vertex transparency (alpha)",GraphBasedResultViewSettings.class,"getVertexTransparency","setVertexTransparency"),
				new PropertyDescriptor("backgroun colour",GraphBasedResultViewSettings.class,"getBackground","setBackground")
			};
				
			//PropertyDescriptor[] properties = java.beans.Introspector.getBeanInfo(Person.class).getPropertyDescriptors();
			properties[0].setPropertyEditorClass(LayoutEditor.class);
			
			return properties;
		}
		catch (Exception x) {
			Logger.getLogger(this.getClass()).error("Exception initializing settings",x);
			return new PropertyDescriptor[0];
		}
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
		font4participants =  new Font("Monospaced", Font.PLAIN,10);
		font4nonparticipants =  new Font("Monospaced", Font.ITALIC,10);
		font4edges =  new Font("Monospaced", Font.PLAIN,10);
		minBoxWidth = 120;
		boxHeight4participants = 36;
		boxHeight4nonparticipants = 24;
		vertexTransparency = 200;
		vertexSaturation = (float) 0.8;
		vertexBrightness = (float) 0.8;
		background = Color.WHITE;
		
	}

}
