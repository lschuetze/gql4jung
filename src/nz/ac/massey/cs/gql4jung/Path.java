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

import java.util.ArrayList;
import java.util.List;
/**
 * A path is a sequence of edges connecting vertices.
 * @author jens dietrich
 */
public class Path  {


	private static List<Edge> EMPTY_LIST = new ArrayList<Edge>(0);
	private Vertex end, start;
	private List<Edge> edges = null;
		
	
	public Path() {
		super();
		edges = new ArrayList<Edge>();
	}
	// create an empty path
	public Path(Vertex v) {
		super();
		this.edges = EMPTY_LIST;
		this.start = v;
		this.end = v;
	}
	// create an edge path
	public Path(Edge e) {
		super();
		this.edges = new ArrayList<Edge>(1);
		this.edges.add(e);
		this.start = (Vertex) e.getStart();
		this.end = (Vertex) e.getEnd();
	}
	private Path(int size) {
		super();
		edges = new ArrayList<Edge>(size);
	}
	
	public void setStart(Vertex start)
	{
		this.start=start;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		Path other = (Path) obj;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges))
			return false;
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
		return true;
	}
	public void setEnd(Vertex end)
	{
		this.end=end;
	}
	public void setEdges(List<Edge> edges)
	{
		this.edges = edges;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public Vertex getEnd() {
		return end;
	}

	public Vertex getStart() {
		return start;
	}

	public Path addAtEnd(Edge e) {
		if (this.edges.size()>0) {
			assert (edges.get(edges.size()-1).getEnd()==e.getStart());
		}
		Path path = new Path(this.edges.size()+1);
		path.edges.addAll(this.edges);
		path.edges.add(e);
		path.start = this.start;
		path.end = (Vertex) e.getEnd();
		return path;
	}

	public Path addAtStart(Edge e) {
		if (this.edges.size()>0) {
			assert (edges.get(0).getStart()==e.getEnd());
		}
		Path path = new Path(this.edges.size()+1);
		path.edges.add(e);
		path.edges.addAll(this.edges);
		path.start = (Vertex) e.getStart();
		path.end = this.end;
		return path;
	}
	
	public boolean isEdge() {
		return this.edges.size()==1;
	}
	
	public boolean isEmpty() {
		return this.edges.size()==0;
	}
	
	public int size() {
		return this.edges.size();
	}

	@Override
	public String toString() {
		return new StringBuffer()
			.append("Path[length=")
			.append(size())
			.append(",")
			.append(getStart())
			.append(" -> ")
			.append(getEnd())
			.append("]")
			.toString();
	}
	/**
	 * Get an ordered list of vertices.
	 * @return
	 */
	public List<Vertex> getVertices() {
		List<Vertex> l = new ArrayList<Vertex>(this.edges.size()+1);
		l.add(this.getStart());
		for (Edge e:edges) {
			l.add((Vertex)e.getEnd());
		}
		return l;
	}
}
