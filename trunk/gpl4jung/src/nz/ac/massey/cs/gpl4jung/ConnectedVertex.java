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
/**
 * Simple data structure representing a vertex and a link (edge or path)
 * leading to this vertex.
 * @author jens.dietrich@gmail.com
 *
 * @param <ConnectionType>
 */
public class ConnectedVertex<ConnectionType> {
	private ConnectionType link = null;
	private Vertex vertex = null;
	
	public ConnectedVertex(ConnectionType link, Vertex vertex) {
		super();
		this.link = link;
		this.vertex = vertex;
	}
	public ConnectionType getLink() {
		return link;
	}
	public void setLink(ConnectionType link) {
		this.link = link;
	}
	public Vertex getVertex() {
		return vertex;
	}
	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}
}
