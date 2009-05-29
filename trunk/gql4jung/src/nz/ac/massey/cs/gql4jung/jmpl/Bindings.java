package nz.ac.massey.cs.gql4jung.jmpl;

import nz.ac.massey.cs.gql4jung.Path;
import nz.ac.massey.cs.gql4jung.Vertex;

/**
 * Data structure for variable bindings.
 * @author jens dietrich
 */
class Bindings  extends Logging {
	public static int SIZE = 100;
	private String[] keys4roles = new String[SIZE];
	private Vertex[] values4roles = new Vertex[SIZE];
	private String[] keys4links = new String[SIZE];
	private Path[] values4links = new Path[SIZE];
	
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
		if (LOG_BIND.isDebugEnabled()) {
			StringBuffer b = new StringBuffer();
			b.append("binding ");
			b.append(k);
			b.append(" -> ");
			b.append(v.toString());
			//LOG_BIND.debug("binding "+k+" -> "+v);
			LOG_BIND.debug(b.toString());
		}
	}
	/**
	 * Add a new entry.
	 */
	public void bind(String k,Path p) {
		assert(k!=null);
		assert(p!=null);		
		keys4links[position]= k;
		values4links[position] = p;
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
	public java.util.Map<String,Path> getPathBindingsAsMap() {
		java.util.Map<String,Path> map = new java.util.Hashtable<String,Path>();
		for (int i=position;i>-1;i--) {
			if (keys4links[i]!=null)
				map.put(keys4links[i],values4links[i]);
		}
		return map;
	}
	/**
	 * Goes one level down.
	 */
	public void gotoNextLevel() {
		position = position+1;
	}
	/**
	 * Goes one level up.
	 */
	public void backtrack() {
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
