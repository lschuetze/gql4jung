package nz.ac.massey.cs.gql4jung;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DefaultMotif implements Motif {
	
	private List<String> roles = new ArrayList<String>();
	private List<String> pathRoles = new ArrayList<String>();

	private Collection<GroupByClause> groupByClauses = new ArrayList<GroupByClause>();
	private Collection<Processor> graphProcessors = new ArrayList<Processor>();
	private List<Constraint> constraints = new ArrayList<Constraint>();
	
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public List<Constraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	@Override
	public Collection<GroupByClause> getGroupByClauses() {	
		return groupByClauses;
	}
	public void setGroupByClauses(Collection<GroupByClause> groupBy){
		this.groupByClauses = groupBy;
	}
	@Override
	public Collection<Processor> getGraphProcessor() {
		return graphProcessors;
	}
	public void setGraphProcessor(Collection<Processor> processors){
		this.graphProcessors = processors; 
	}
	public List<String> getPathRoles() {
		return pathRoles;
	}
	public void setPathRoles(List<String> pathRoles) {
		this.pathRoles = pathRoles;
	}

}
