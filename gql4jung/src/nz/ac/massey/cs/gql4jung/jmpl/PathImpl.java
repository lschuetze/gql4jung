package nz.ac.massey.cs.gql4jung.jmpl;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.gql4jung.Path;

public class PathImpl implements Path {
	private Vertex end, start;
	private List<Edge> edges = new ArrayList<Edge>();
		
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
		PathImpl other = (PathImpl) obj;
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
	@Override
	public List<Edge> getEdges() {
		return edges;
	}

	@Override
	public Vertex getEnd() {
		return end;
	}

	@Override
	public Vertex getStart() {
		return start;
	}

}
