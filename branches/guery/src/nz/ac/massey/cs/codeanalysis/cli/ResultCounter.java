/**
 * Copyright 2010 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.codeanalysis.cli;

import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.ResultListener;
/**
 * Simple listener that just calls results.
 * @author jens dietrich
 */
public class ResultCounter implements ResultListener<TypeNode,TypeReference> {
	private int counter = 0;
	private long started = -1;
	@Override
	public synchronized void done() {
		Run.log("Results found: ",counter);
		Run.log("Time needed to compute: ",System.currentTimeMillis()-started);
		counter=0;
		started=-1;
	}

	@Override
	public synchronized boolean  found(MotifInstance<TypeNode,TypeReference> instance) {
		if (started==-1) {
			started = System.currentTimeMillis();
		} 
		counter = counter+1;
		return true;
	}

	@Override
	public void progressMade(int progress, int total) {}

}
