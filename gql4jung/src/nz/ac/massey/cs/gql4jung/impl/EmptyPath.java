package nz.ac.massey.cs.gql4jung.impl;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.gql4jung.Path;

/**
 * Empty path.
 * @author jens
 */
public class EmptyPath implements Path {

	private Vertex vertex = null;
	private static List<Edge> EMPTY_LIST = new ArrayList<Edge>(0);
	
	public EmptyPath() {
		super();
	}
	
	public EmptyPath(Vertex vertex) {
		super();
		this.vertex = vertex;
	}
	
	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	@Override
	public List<Edge> getEdges() {
		return EMPTY_LIST;
	}

	@Override
	public Vertex getEnd() {
		return vertex;
	}

	@Override
	public Vertex getStart() {
		return vertex;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vertex == null) ? 0 : vertex.hashCode());
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
		EmptyPath other = (EmptyPath) obj;
		if (vertex == null) {
			if (other.vertex != null)
				return false;
		} else if (!vertex.equals(other.vertex))
			return false;
		return true;
	}

}
