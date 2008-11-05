/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gpl4jung;

import edu.uci.ics.jung.graph.Vertex;

public interface MotifInstance {
	/**
	 * Get the instantiated motif.
	 * @return
	 */
	Motif getMotif();
	/**
	 * Get a vertex for a given role name (id attribute in query).
	 * The role name is the id of the node in the query.
	 * @param roleName
	 * @return
	 */
	Vertex getVertex(String roleName);
	
	
}
