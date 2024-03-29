/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung;

/**
 * Custom edge class.
 * @author jens dietrich
 */

public class Edge extends GraphElement{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	private String type = null;
	private Vertex start = null;
	private Vertex end = null;
	
	public Edge(String id, Vertex end, Vertex start) {
		super(id);
		this.setEnd(end);
		this.setStart(start);
	}
	public Edge() {
		super();
	}

	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Vertex getStart() {
		return start;
	}
	public Vertex getEnd() {
		return end;
	}
	public void setStart(Vertex start) {
		if (this.start!=null) {
			boolean success = start.removeOutEdge(this);
			assert success;
		}
		this.start = start;
		start.addOutEdge(this);
	}
	public void setEnd(Vertex end) {
		if (this.end!=null) {
			boolean success = start.removeInEdge(this);
			assert success;
		}
		this.end = end;
		end.addInEdge(this);
	}
	
	public String toString() {
		return new StringBuffer() 			
			.append(this.getId())
			.append(':')
			.append('[')
			.append(this.start)
			.append(" ")
			.append(this.type)
			.append(" ")
			.append(this.end)
			.append(']')
			.toString();
	}
	public void copyValuesTo(Edge e) {
		e.setType(type);
	}
}
