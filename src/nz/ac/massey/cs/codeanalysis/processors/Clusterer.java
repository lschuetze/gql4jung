/**
 * Copyright 2010 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.codeanalysis.processors;

import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;

/**
 * Implementation class for processing clusters in graph.
 * @author jens dietrich
 */
public class Clusterer extends nz.ac.massey.cs.gql4jung.processors.Clusterer<TypeNode,TypeReference> {
	
	public Clusterer() {
		super();
	}
	

	protected  void annotateWithClusterLabel(TypeNode vertex,String clusterLabel) {
		vertex.setCluster(clusterLabel);
	}


}
