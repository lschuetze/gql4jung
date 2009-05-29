package nz.ac.massey.cs.gql4jung.jmpl;

import java.util.List;

import nz.ac.massey.cs.gql4jung.Constraint;
/**
 * Agenda for resolving constraints.
 * @author jens dietrich
 */
class Agenda {
	private List<Constraint> constraints = null;
	private int cursor = 0;
	
	public Agenda(List<Constraint> constraints) {
		super();
		this.constraints = constraints;
	}
	boolean isDone() {
		return cursor==constraints.size();
	}
	Constraint next() {
		Constraint c = constraints.get(cursor);
		cursor=cursor+1;
		return c;
	}
	void backtrack() {
		cursor=cursor-1;
	}
	
}
