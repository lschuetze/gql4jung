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

import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.browser.PropertyBean;
import nz.ac.massey.cs.gql4jung.browser.QueryView;

/**
 * Query source code view.
 * @author jens dietrich
 */

public class XMLSourceQueryView extends QueryView {
	
	private JTextPane textPane = new JTextPane();

	public XMLSourceQueryView() {
		super();
		this.setLayout(new GridLayout(1,1));
		this.add(new JScrollPane(textPane));
		textPane.setEditable(false);
	}

	@Override
	public void display(Motif query, String source) {
		textPane.setText(source);
	}

	@Override
	public String getName() {
		return "motif source";
	}

	@Override
	public PropertyBean getSettings() {
		return null;
	}

}
