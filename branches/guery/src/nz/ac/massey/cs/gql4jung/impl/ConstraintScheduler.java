/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */



package nz.ac.massey.cs.gql4jung.impl;

import java.util.List;
import edu.uci.ics.jung.graph.Graph;
import nz.ac.massey.cs.gql4jung.Constraint;
import nz.ac.massey.cs.gql4jung.Motif;

/**
 * Scheduler for constraints. Scheduling constraints means listing them in an
 * optimizes fashion. 
 * @author jens dietrich
 * 
 */

public interface ConstraintScheduler {
    /**
     * Get the constraints for the motif. 
     * @param g the graph
     * @param pattern a motif
     * @return a list of constraints
     */
    public List<Constraint> getConstraints(Graph g,Motif motif);

}