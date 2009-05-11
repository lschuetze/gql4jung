package nz.ac.massey.cs.gql4jung.jmpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Untility to reuse objects.
 * @author jens dietrich
 */
public abstract class ObjectPool<T> {
	private int maxSize = 42;
	
	public ObjectPool(int maxSize) {
		super();
		this.maxSize = maxSize;
	}

	private List<T> pool = new ArrayList<T>();
	public synchronized T borrow() {
		if (!pool.isEmpty()) {
			return pool.remove(pool.size()-1);
		}
		else {
			return createNew();
		}
	} 
	public abstract T createNew();
	
	public synchronized void recycle(T obj) {
		reset(obj);
		if (pool.size() < this.maxSize) {
			pool.add(obj);
		}
	}
	public abstract void reset(T obj) ;
}
