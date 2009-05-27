
package nz.ac.massey.cs.gql4jung.jmpl;

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