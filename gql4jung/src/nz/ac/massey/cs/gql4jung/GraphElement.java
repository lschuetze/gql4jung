package nz.ac.massey.cs.gql4jung;

/**
 * Abstract superclass for custom vertex and edge classes.
 * @author jens dietrich
 */
public class GraphElement {
	private String id = null;
	
	public GraphElement() {
		super();
	}
	public GraphElement(String id) {
		super();
		this.id = id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
}
