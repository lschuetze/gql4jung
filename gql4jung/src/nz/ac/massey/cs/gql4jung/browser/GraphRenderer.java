/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.browser;

import java.awt.Font;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Renderer (factory) for graph. 
 * @author Jens Dietrich
 */

public class GraphRenderer {
	
	static Icon getIcon(String name) {
		URL url = GraphRenderer.class.getResource("/nz/ac/massey/cs/gql4jung/browser/icons/"+name);
		return new ImageIcon(url);
	}

	
	final static int FONT_SIZE = 10;
	final static String FONT_TYPE = "Monospaced";
	final static Font CORE = new Font(FONT_TYPE, Font.PLAIN, FONT_SIZE);
	final static Font NON_CORE = new Font(FONT_TYPE, Font.PLAIN, FONT_SIZE);
	final static Font OTHER = new Font(FONT_TYPE, Font.ITALIC, FONT_SIZE);
	final static Icon ICON_CLASS_C = getIcon("class.gif");
	final static Icon ICON_CLASS_BW = getIcon("class-bw.gif");
	final static Icon ICON_INTERFACE_C = getIcon("interface.gif");
	final static Icon ICON_INTERFACE_BW = getIcon("interface-bw.gif");
	

}
