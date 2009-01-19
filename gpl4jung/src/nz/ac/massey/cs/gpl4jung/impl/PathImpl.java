package nz.ac.massey.cs.gpl4jung.impl;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.gpl4jung.Path;

public class PathImpl implements Path {
	private Vertex end, start;
	private List<Edge> edges = new ArrayList<Edge>();
		
	public void setStart(Vertex start)
	{
		this.start=start;
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
