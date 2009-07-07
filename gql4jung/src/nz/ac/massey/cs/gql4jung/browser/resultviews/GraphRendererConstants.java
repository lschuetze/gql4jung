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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Renderer (factory) for graph. 
 * @author Jens Dietrich
 */

public class GraphRendererConstants {
	
	static Icon getIcon(String name) {
		URL url = GraphRendererConstants.class.getResource("/nz/ac/massey/cs/gql4jung/browser/icons/"+name);
		return new ImageIcon(url);
	}

	final static Stroke STROKE_NORMAL = new BasicStroke(1);
	final static Stroke STROKE_BOLD = new BasicStroke(2);
	final static int BOX_HEIGHT_UNIT = 12;
	final static int MIN_BOX_WIDTH = 120;

}
