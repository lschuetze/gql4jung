package nz.ac.massey.cs.gpl4jung;

import java.util.ArrayList;
import java.util.List;

public class DefaultMotif implements Motif {
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public List<LinkConstraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<LinkConstraint> constraints) {
		this.constraints = constraints;
	}
	private List<String> roles = new ArrayList<String>();
	private List<LinkConstraint> constraints = new ArrayList<LinkConstraint>();


}
