package nz.ac.massey.cs.gql4jung;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nz.ac.massey.cs.processors.Processor;

public class DefaultMotif implements Motif {
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
	private List<String> roles = new ArrayList<String>();
	private Collection<GroupByClause> groupByClasses = new ArrayList<GroupByClause>();
	private Collection<Processor> graphProcessors = new ArrayList<Processor>();
	
	private List<Constraint> constraints = new ArrayList<Constraint>();
	@Override
	public Collection<GroupByClause> getGroupByClauses() {	
		return groupByClasses;
	}
	public void setGroupByClauses(Collection<GroupByClause> groupBy){
		this.groupByClasses = groupBy;
	}
	@Override
	public Collection<Processor> getGraphProcessor() {
		return graphProcessors;
	}
	public void setGraphProcessor(Collection<Processor> processors){
		this.graphProcessors = processors; 
	}
	

}
