package nz.ac.massey.cs.gpl4jung.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.Motif;

public class ConstraintSchedulerImpl implements ConstraintScheduler {

	@Override
	public List<LinkConstraint> getConstraints(Motif motif) {
		List<LinkConstraint> constraints = new ArrayList<LinkConstraint>();
		for(Iterator itr = motif.getConstraints().iterator();itr.hasNext();){
			LinkConstraint nextConstraint = (LinkConstraint) itr.next();
			constraints.add(nextConstraint);
		}
		return constraints;
	}

}
