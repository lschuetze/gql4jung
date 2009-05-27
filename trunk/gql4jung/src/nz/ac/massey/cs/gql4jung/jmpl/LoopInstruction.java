package nz.ac.massey.cs.gql4jung.jmpl;

import nz.ac.massey.cs.gql4jung.Constraint;
/**
 * Instruction used by the constraint scheduler to tell the engine that it must
 * iterate over vertices to bind a certain role.
 * @author jens dietrich
 */
public class LoopInstruction implements Constraint {
	@Override
	public String toString() {
		return "Loop instruction for role: "+role;
	}

	private String role = null;

	public LoopInstruction(String role) {
		super();
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
