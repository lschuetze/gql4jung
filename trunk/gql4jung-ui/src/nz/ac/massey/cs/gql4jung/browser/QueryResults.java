package nz.ac.massey.cs.gql4jung.browser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import nz.ac.massey.cs.gpl4jung.MotifInstance;
import nz.ac.massey.cs.gpl4jung.ResultListener;

public class QueryResults implements ResultListener{
	
	public class Cursor {
		public Cursor(int major, int minor) {
			super();
			this.major = major;
			this.minor = minor;
		}
		public int major = -1;
		public int minor = -1;
	}
	
	private VertexGroupByDefinition groupByDef = new VertexGroupByDefinition() {
		@Override
		public Object getGroupIdentifier(MotifInstance instance) {
			return instance.toString();
		}
	};
	private LinkedHashMap<Object,List<MotifInstance>> results = new LinkedHashMap<Object,List<MotifInstance>>();
	private List<Object> keys = new ArrayList<Object>();
	private int majorCursor = -1;
	private int minorCursor = -1;
	private long lastupdate;
	private long minTimeBetweenEvents = 50;
	
	public interface QueryResultListener {
		public void resultsChanged(QueryResults source);
	} 
	private List<QueryResultListener> listeners = new Vector<QueryResultListener>();

	public long getMinTimeBetweenEvents() {
		return minTimeBetweenEvents;
	}
	public void setMinTimeBetweenEvents(long minTimeBetweenEvents) {
		this.minTimeBetweenEvents = minTimeBetweenEvents;
	}
	public void addListener(QueryResultListener l) {
		listeners.add(l);
	}
	public void removeListener(QueryResultListener l) {
		listeners.remove(l);
	}
	
	public synchronized void reset() {
		this.results.clear();
		this.keys.clear();
		majorCursor = -1;
		minorCursor = -1;
		
		// inform listeners
		callback();			
	}
	public synchronized boolean found(MotifInstance instance) {
		Object key = groupByDef.getGroupIdentifier(instance);
		List<MotifInstance> instances = results.get(key);
		if (instances==null) {
			instances = new ArrayList<MotifInstance>();
			results.put(key,instances);
			keys.add(key);
		}
		instances.add(instance);
		
		// inform listeners
		callback();	
		
		return true;
	}
	private void callback() {
		long t = System.currentTimeMillis();
		if (t-lastupdate > minTimeBetweenEvents) { 
			lastupdate = t;
			for (QueryResultListener l:this.listeners) {
				l.resultsChanged(this);
			}	
		} 
	}
	public synchronized int getNumberOfGroups() {
		return results.size();
	}
	public synchronized int getNumberOfInstances(int groupIndex) {
		if (groupIndex==-1) return 0;
		Object key = keys.get(groupIndex);
		if (key==null) return 0;
		List<MotifInstance> instances =  results.get(key);
		return instances==null?0:instances.size();
	}
	@Override
	public void done() {
		// TODO Auto-generated method stub
		
	}
	
	public Cursor getCursor() {
		return new Cursor(this.majorCursor,this.minorCursor);
	}

	// cursor operations
	public synchronized Cursor setInitialCursor() {
		if (this.getNumberOfGroups()>0 && this.getNumberOfInstances(0)>0) {
			majorCursor=0;
			minorCursor=0;
		}
		return new Cursor(this.majorCursor,this.minorCursor);
	}
	// cursor operations
	public synchronized boolean hasNextMajorInstance() {
		return majorCursor<(this.getNumberOfGroups()-1) && this.getNumberOfInstances(majorCursor+1)>0;
	}
	public synchronized Cursor nextMajorInstance() {
		if (hasNextMajorInstance()) {
			majorCursor=majorCursor+1;
			minorCursor=0;
		}
		return new Cursor(this.majorCursor,this.minorCursor);
	}
	public synchronized boolean hasPreviousMajorInstance() {
		return majorCursor>0 && this.getNumberOfInstances(majorCursor-1)>0;
	}
	public synchronized Cursor previousMajorInstance() {
		if (hasPreviousMajorInstance()) {
			majorCursor=majorCursor-1;
			minorCursor=0;
		}
		return new Cursor(this.majorCursor,this.minorCursor);
	}
	public synchronized boolean hasNextMinorInstance() {
		return minorCursor<(this.getNumberOfInstances(majorCursor)-1);
	}
	public synchronized Cursor nextMinorInstance() {
		if (hasNextMinorInstance()) {
			minorCursor=minorCursor+1;
		}
		return new Cursor(this.majorCursor,this.minorCursor);
	}
	public synchronized boolean hasPreviousMinorInstance() {
		return 0<minorCursor;
	}
	public synchronized Cursor previousMinorInstance() {
		if (hasPreviousMinorInstance()) {
			minorCursor=minorCursor-1;
		}
		return new Cursor(this.majorCursor,this.minorCursor);
	}
	
	public synchronized MotifInstance getInstance(Cursor cursor) {
		Object key = this.keys.get(cursor.major);
		return results.get(key).get(cursor.minor);
	}

	
}
