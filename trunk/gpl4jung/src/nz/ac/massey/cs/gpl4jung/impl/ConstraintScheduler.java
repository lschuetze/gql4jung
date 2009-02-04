
package nz.ac.massey.cs.gpl4jung.impl;

import java.util.List;

import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.Motif;

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
    public List<LinkConstraint> getConstraints(Motif motif);

}