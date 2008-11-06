/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gpl4jung;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
/**
 * Optimises the constraints. This is usually done by rearranging the order of the constraints
 * based on the graph (statistics), and existing bindings (resolved variables). 
 * @author jens.dietrich@gmail.com
 *
 */
public interface QueryOptimizer {
	void optimize(Motif query,MotifInstance partialInstance,List<Constraint> remainingConstraints,Graph graph);
}
