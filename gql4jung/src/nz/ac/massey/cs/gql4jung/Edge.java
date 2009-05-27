package nz.ac.massey.cs.gql4jung;

/**
 * Custom edge class.
 * @author jens dietrich
 */

public class Edge extends GraphElement{
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
			.append("->")
			.append(this.end)
			.append(']')
			.toString();
	}

}
