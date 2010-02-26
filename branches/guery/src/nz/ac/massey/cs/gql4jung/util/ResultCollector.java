/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.util;

import java.util.List;
import java.util.Vector;

import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.ResultListener;
import nz.ac.massey.cs.gql4jung.Vertex;

public class ResultCollector<V extends Vertex<E>,E extends Edge<V>> implements ResultListener<V,E> {

	private long creationTime = System.currentTimeMillis();
	private boolean logProgress = false;
	private List<MotifInstance<V,E>> instances = new Vector<MotifInstance<V,E>>();
	
	public List<MotifInstance<V,E>> getInstances() {
		return instances;
	}

	public void setInstances(List<MotifInstance<V,E>> instances) {
		this.instances = instances;
	}

	@Override
	public void done() {
		// nothing to do here
	}

	@Override
	public boolean found(MotifInstance<V,E> instance) {
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
