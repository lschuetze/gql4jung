package nz.ac.massey.cs.gpl4jung;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Map<String, String> coreInfo = new HashMap<String, String>();
	private List<Constraint> constraints = new ArrayList<Constraint>();
	public boolean isCore(String role) {
		if(coreInfo.get(role).equals("true")){
			return true;
		}
		else 
			return false;
	}
	public Map<String, String> getCoreInfo() {
		return coreInfo;
	}
	public void setCoreInfo(Map<String, String> coreInfo) {
		this.coreInfo = coreInfo;
	}

}
