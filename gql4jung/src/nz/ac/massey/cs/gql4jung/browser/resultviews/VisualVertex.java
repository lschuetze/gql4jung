/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.browser.resultviews;

import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.browser.RankedVertex;

/**
 * Custom vertex class for visualisation.
 * @author jens dietrich
 */

public class VisualVertex extends Vertex implements RankedVertex {
	private String role = null;
	private boolean inMotif = false;
	private int distanceFromMotif = 0;

	public int getDistanceFromMotif() {
		return distanceFromMotif;
	}

	public void setDistanceFromMotif(int distanceFromMotif) {
		this.distanceFromMotif = distanceFromMotif;
	}

	public boolean isInMotif() {
		return inMotif;
	}

	public void setInMotif(boolean inMotif) {
		this.inMotif = inMotif;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public int getDegree() {
		return distanceFromMotif;
	}
	
}
