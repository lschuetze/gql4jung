package nz.ac.massey.cs.gpl4jung.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.gpl4jung.DefaultMotif;
import nz.ac.massey.cs.gpl4jung.LinkConstraint;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.MotifInstance;



public class MotifInstanceImpl implements MotifInstance {
	Motif motif = new DefaultMotif();
	
	
	public Object getLink(LinkConstraint constraint) {
		String id = constraint.getID();
		for (Iterator iter = replacements.entrySet().iterator(); iter.hasNext();) {
            Map.Entry nextEntry = (Map.Entry) iter.next();
            if(replacements.containsKey(id)){
            	Object o = (Object) replacements.get(id);
            	return o;
            }
		}
		return null;
	}
	
		
	@Override
	public Motif getMotif() {
		return motif;
	}
	
	public void setMotif(Motif motif){
		this.motif = motif;
	}

	@Override
	public Vertex getVertex(String roleName) {
		for (Iterator iter = replacements.entrySet().iterator(); iter.hasNext();) {
            Map.Entry nextEntry = (Map.Entry) iter.next();
            if(replacements.containsKey(roleName)){
            	Vertex v = (Vertex) replacements.get(roleName);
            	return v;
            }
		}
		return null;
	}
	/**
     * Add a replacement.
     * 
     * @param key to retrieve the vertex/link
     *            
     * @param instance link/vertex
     *            
     */
    public void add(String key, Object instance) {
        replacements.put(key, instance);
    }
	public void addAll(Map bindings){
		for (Iterator iter = bindings.entrySet().iterator(); iter.hasNext();) {
            Map.Entry nextEntry = (Map.Entry) iter.next();
            add((String) nextEntry.getKey(), nextEntry.getValue());
		}
	}
private Map<String, Object> replacements = new HashMap<String, Object>();






}
