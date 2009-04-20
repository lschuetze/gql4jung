
package nz.ac.massey.cs.gql4jung.impl;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;

import nz.ac.massey.cs.gql4jung.Constraint;
import nz.ac.massey.cs.gql4jung.Motif;

/**
 * Scheduler for constraints. Scheduling constraints means listing them in an
 * optimizes fashion. 
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich" target="_top">Jens Dietrich </a>
 * 
 */

public interface ConstraintScheduler {
    /**
     * Get the constraints for the motif. 
     * @param pattern a motif
     * @return a list of constraints
     */
    public List<Constraint> getConstraints(Motif motif);
    // static - only once
    public List<Constraint> prepare (Graph g,List<Constraint> constraints); // sort, put abstract before concrete, edge before path
    // dynamic
    public Constraint selectNext (Graph g, List<Constraint> agenda, Bindings bindings);

}