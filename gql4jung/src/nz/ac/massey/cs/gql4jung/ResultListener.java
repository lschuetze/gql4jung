/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung;
/**
 * Interface used to notify clients that a new result has been found. 
 * @author jens dietrich
 */
public interface ResultListener {
	/**
	 * Notify listener that a new result has been found. 
	 * @param instance
	 * @return a boolean indicating whether to look for more results.
	 */
	boolean found(MotifInstance instance);
	/**
	 * Notify listener about progress made. 
	 * @param progress the number of tasks performed
	 * @param total the total number of steps that have to be performed
	 */
	void progressMade(int progress,int total);
	/**
	 * Notify listener that there will be no more results.
	 */
	void done();
}
