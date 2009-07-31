/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.benchmarking;

import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.ResultListener;
/**
 * Utility to count results without recording them.
 * @author jens
 *
 */
public class ResultCounter implements ResultListener {

	private int counter = 0;
	public int getCounter() {
		return counter;
	}

	@Override
	public void done() {
	}

	@Override
	public boolean found(MotifInstance instance) {
		this.counter=counter+1;
		return true;
	}

	@Override
	public void progressMade(int progress, int total) {}

}
