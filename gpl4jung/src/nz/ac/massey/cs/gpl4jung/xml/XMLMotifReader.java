/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gpl4jung.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import nz.ac.massey.cs.gpl4jung.*;
import nz.ac.massey.cs.gpl4jung.constraints.EdgeConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.PathConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.PropertyTerm;
import nz.ac.massey.cs.gpl4jung.constraints.SimplePropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.ValueTerm;
import nz.ac.massey.cs.gpl4jung.xml.Query.Vertex.Property;

public class XMLMotifReader implements MotifReader {

	@Override
	public Motif read(InputStream source) throws MotifReaderException {
		try {
			DefaultMotif motif = new DefaultMotif();
			List<String> v_roles = new ArrayList<String>();
			List<Constraint> constraints = new ArrayList<Constraint>();
			motif.setRoles(v_roles);
			motif.setConstraints(constraints);
			PropertyTerm pt = null;
//			ValueTerm term2 = null;
			//unmarshalling xml query
			JAXBContext jc= JAXBContext.newInstance("nz.ac.massey.cs.gpl4jung.xml");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			Query q= (Query)unmarshaller.unmarshal(source);
			//System.out.println(q);
			
			
			for (Object o:q.getVertexOrPathOrEdge()) {
				//getting roles (vertex id) from query
				if (o instanceof Query.Vertex) {
					Query.Vertex v = (Query.Vertex)o;
					v_roles.add(v.id); 
					//getting vertex property constraints
					for(Iterator itr=v.getProperty().iterator(); itr.hasNext();){
						Query.Vertex.Property p = (Query.Vertex.Property) itr.next();
						PropertyTerm term1 = new PropertyTerm(p.getKey());
						ValueTerm term2 = new ValueTerm(p.getValue());
						SimplePropertyConstraint<Vertex> pc = new SimplePropertyConstraint<Vertex>();
						pc.setOwner(v.id);
						pc.setTerms(term1, term2);
						constraints.add(pc);
					}
				}

				//getting path constraint from query
				else if (o instanceof Query.Path) {
					PathConstraint pathConstraint = new PathConstraint();
					Query.Path p = (Query.Path)o;
					//System.out.println("path from " + p.getFrom() + " to " + p.getTo());
					if(p.getMinLength()!=null && p.getMaxLength()!=null){
						pathConstraint.setMinLength(p.getMinLength());
						pathConstraint.setMaxLength(p.getMaxLength());
					}
					pathConstraint.setSource(p.getFrom());
					pathConstraint.setTarget(p.getTo());
					//getting path property constraint
					Query.Path.Property pp = p.getProperty();
					if(pp!=null){
						PropertyTerm term1 = new PropertyTerm(pp.getKey());
						ValueTerm term2 = new ValueTerm(pp.getValue());
						SimplePropertyConstraint<Edge> pathPropConstraint = new SimplePropertyConstraint<Edge>();
						pathPropConstraint.setOwner(pathConstraint.getPathID());
						pathPropConstraint.setTerms(term1, term2);
						constraints.add(pathPropConstraint);
					}
					constraints.add(pathConstraint);
				}
				else if (o instanceof Query.Condition) {
					Query.Condition p = (Query.Condition)o;
					LinkConstraint condition = null;
					constraints.add((LinkConstraint) p.getAttribute());
					condition.setPredicate(p.getPredicate());
					constraints.add(condition);					
				}
				else if (o instanceof Query.Edge){
					EdgeConstraint edgeConstraint = new EdgeConstraint();
					Query.Edge e = (Query.Edge) o;
					edgeConstraint.setSource(e.getSource());
					edgeConstraint.setTarget(e.getTarget());
					//getting edge properties
					Query.Edge.Property ee = (Query.Edge.Property)o;
					PropertyTerm term1 = new PropertyTerm(ee.getKey());
					ValueTerm term2 = new ValueTerm(ee.getValue());
					SimplePropertyConstraint<Edge> edgePropConstraint = new SimplePropertyConstraint<Edge>();
					edgePropConstraint.setTerms(term1,term2);
					edgePropConstraint.setOwner(edgeConstraint.getEdgeID());
					constraints.add(edgeConstraint);
					constraints.add(edgePropConstraint);
				}
				else if (o instanceof Query.ExistsNot){
					Query.ExistsNot e = (Query.ExistsNot)o;
					v_roles.add(e.getVertex().getId()); //gets vertex id in complex condition of Exists not
				}
				else if (o instanceof Query.Not){
					Query.Not n = (Query.Not)o;
					LinkConstraint notcond = null;
					notcond.setPredicate(n.getCondition().getPredicate()); //gets predicate from NOT CONDITION
					constraints.add((LinkConstraint) n.getCondition().getAttribute()); //gets list of attributes from NOT CONDITION ATTRIBUTEs
					constraints.add(notcond);
					
				}			
			}
			
			return motif;
		
		} catch (JAXBException e) {
			throw new MotifReaderException("exception reading motif from xml",e);
		}	
				
	}
}
