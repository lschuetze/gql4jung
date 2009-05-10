package nz.ac.massey.cs.gql4jung.jmpl;

import nz.ac.massey.cs.gql4jung.LinkConstraint;
import edu.uci.ics.jung.graph.Vertex;

/**
 * Data structure for variable bindings.
 * @author jens dietrich
 */
public class Bindings  {
	public static int SIZE = 100;
	private String[] keys4roles = new String[SIZE];
	private Vertex[] values4roles = new Vertex[SIZE];
	private LinkConstraint[] keys4links = new LinkConstraint[SIZE];
	private Object[] values4links = new Object[SIZE];
	
	private int position = 0;

	/**
	 * Constructor.
	 * @param parent
	 */
	public Bindings() {
		super();
	}
	/**
	 * Lookup the binding for a given key.
	 */
	public Vertex lookup(String k) {
		for (int i=position;i>-1;i--) {
			if (k.equals(keys4roles[i])) 
				return values4roles[i];
		}
		return null;
	}
	/**
	 * Add a new entry.
	 */
	public void bind(String k,Vertex v) {
		assert(k!=null);
		assert(v!=null);		
		keys4roles[position]= k;
		values4roles[position] = v;
		//System.out.println("binding "+k+" -> "+v);
	}
	/**
	 * Add a new entry.
	 */
	public void bind(LinkConstraint k,Object o) {
		assert(k!=null);
		assert(o!=null);		
		keys4links[position]= k;
		values4links[position] = o;
		//System.out.println("binding "+k+" -> "+v);
	}
	/**
	 * Indicates whether the values contain a given value.
	 */
	public boolean containsValue(String role) {
		for (int i=position;i>-1;i--) {
			if (role.equals(values4roles[i])) 
				return true;
		}
		return false;
	}
	/**
	 * Converts the role bindings to a map.
	 */
	public java.util.Map<String,Vertex> getRoleBindingsAsMap() {
		java.util.Map<String,Vertex> map = new java.util.Hashtable<String,Vertex>();
		for (int i=position;i>-1;i--) {
			if (keys4roles[i]!=null)
				map.put(keys4roles[i],values4roles[i]);
		}
		return map;
	}
	public java.util.Map<LinkConstraint,Object> getLinkBindingsAsMap() {
		java.util.Map<LinkConstraint,Object> map = new java.util.Hashtable<LinkConstraint,Object>();
		for (int i=position;i>-1;i--) {
			if (keys4links[i]!=null)
				map.put(keys4links[i],values4links[i]);
		}
		return map;
	}
	/**
	 * Goes one level down.
	 */
	public void gotoChildLevel() {
		position = position+1;
	}
	/**
	 * Goes one level up.
	 */
	public void gotoParentLevel() {
		this.keys4roles[position]=null;
		this.values4roles[position]=null;
		this.keys4links[position]=null;
		this.values4links[position]=null;
		position = position-1;
	}

	/**
	 * Get the value at a certain position.
	 * Useful for debugging.
	 * @param pos a position
	 * @return a value
	 */
	 public Vertex getValue(int pos) {
		return pos<SIZE?this.values4roles[pos]:null; 
	 }
	/**
	 * Get the key at a certain position.
	 * Useful for debugging.
	 * @param pos a position
	 * @return a key
	 */
	 public String getKey(int pos) {
		return pos<SIZE?this.keys4roles[pos]:null; 
	 }
	 /**
	  * Get the number of bindings.
	  * @return the position
	  */
	 public int getSize() {
		 return position+(keys4roles[position]==null?0:1);
	 }
	 /**
	  * Get the position.
	  */
	 public int getPosition() {
		 return this.position;
	 }
}