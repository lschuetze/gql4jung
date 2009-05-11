package test.nz.ac.massey.cs.gql4jung.jmpl;

import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.ResultListener;

public class ResultCollector implements ResultListener {

	private long creationTime = System.currentTimeMillis();
	private boolean logProgress = false;
	private List<MotifInstance> instances = new ArrayList<MotifInstance>();
	
	public List<MotifInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<MotifInstance> instances) {
		this.instances = instances;
	}

	@Override
	public void done() {
		// nothing to do here
	}

	@Override
	public boolean found(MotifInstance instance) {
		this.instances.add(instance);
		return true;
	}

	@Override
	public void progressMade(int progress, int total) {
		// Switch on progress logging only after query takes more than 5 sec
		if (!logProgress) {
			logProgress =(System.currentTimeMillis()-this.creationTime > 5000);
		}
		if (logProgress) {
			System.out.println("Computing query, "+progress+"/"+total+" done");
		}
	}

}
