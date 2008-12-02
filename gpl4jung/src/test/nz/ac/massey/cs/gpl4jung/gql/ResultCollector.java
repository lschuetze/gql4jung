package test.nz.ac.massey.cs.gpl4jung.gql;

import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.gpl4jung.MotifInstance;
import nz.ac.massey.cs.gpl4jung.ResultListener;

public class ResultCollector implements ResultListener {

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

}
