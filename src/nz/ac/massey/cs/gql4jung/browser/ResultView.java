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

import javax.swing.JPanel;
import edu.uci.ics.jung.graph.DirectedGraph;
import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;
import nz.ac.massey.cs.gql4jung.*;

/**
 * View for results.
 * @author Jens Dietrich
 */
@SuppressWarnings("serial")
public abstract class ResultView extends JPanel {
	public abstract String getName();
	public abstract void display(MotifInstance<TypeNode,TypeReference> instance,DirectedGraph<TypeNode,TypeReference> graph);
	// interface to customise view
	public abstract PropertyBean getSettings();
}
