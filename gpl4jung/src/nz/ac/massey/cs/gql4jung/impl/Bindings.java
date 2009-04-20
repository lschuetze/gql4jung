package nz.ac.massey.cs.gpl4jung.impl;

/**
 * Data structure for variable bindings.
 */
public class Bindings  {
	public static int SIZE = 100;
	private String[] keys = new String[SIZE];
	private Object[] values = new Object[SIZE];
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
	public Object lookup(String k) {
		for (int i=position;i>-1;i--) {
			if (k.equals(keys[i])) 
				return values[i];
		}
		return null;
	}
	/**
	 * Add a new entry.
	 */
	public void bind(String k,Object v) {
		assert(k!=null);
		assert(v!=null);
		
		keys[position]= k;
		values[position] = v;
	}
	/**
	 * Indicates whether the values contain a given value.
	 */
	public boolean containsValue(Object v) {
		for (int i=position;i>-1;i--) {
			if (v.equals(values[i])) 
				return true;
		}
		return false;
	}
	/**
	 * Converts the bindings to a map.
	 */
	public java.util.Map<String,Object> asMap() {
		java.util.Map<String,Object> map = new java.util.Hashtable<String,Object>();
		for (int i=position;i>-1;i--) {
			if (keys[i]!=null)
				map.put(keys[i],values[i]);
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
		keys[position]=null;
		values[position]=null;
		position = position-1;
	}

	/**
	 * Get the value at a certain position.
	 * Useful for debugging.
	 * @param pos a position
	 * @return a value
	 */
	 public Object getValue(int pos) {
		return pos<SIZE?this.values[pos]:null; 
	 }
	/**
	 * Get the key at a certain position.
	 * Useful for debugging.
	 * @param pos a position
	 * @return a key
	 */
	 public String getKey(int pos) {
		return pos<SIZE?this.keys[pos]:null; 
	 }
	 /**
	  * Get the number of bindings.
	  * @return the position
	  */
	 public int getSize() {
		 return position+(keys[position]==null?0:1);
	 }
}
