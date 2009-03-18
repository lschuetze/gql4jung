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

import nz.ac.massey.cs.gpl4jung.*;
import nz.ac.massey.cs.gpl4jung.constraints.ComplexPropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.EdgeConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.NegatedPropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.Operator;
import nz.ac.massey.cs.gpl4jung.constraints.PathConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.PropertyConstraintConjunction;
import nz.ac.massey.cs.gpl4jung.constraints.PropertyConstraintDisjunction;
import nz.ac.massey.cs.gpl4jung.constraints.PropertyTerm;
import nz.ac.massey.cs.gpl4jung.constraints.SimplePropertyConstraint;
import nz.ac.massey.cs.gpl4jung.constraints.ValueTerm;
import nz.ac.massey.cs.gpl4jung.xml.Query.Not;
import nz.ac.massey.cs.gpl4jung.xml.Query.Condition.Attribute;

public class XMLMotifReader implements MotifReader {

	@Override
	public Motif read(InputStream source) throws MotifReaderException {
		try {
			DefaultMotif motif = new DefaultMotif();
			List<String> v_roles = new ArrayList<String>();
			List<Constraint> constraints = new ArrayList<Constraint>();
			motif.setRoles(v_roles);
			motif.setConstraints(constraints);
			
			//unmarshalling xml query
			JAXBContext jc= JAXBContext.newInstance("nz.ac.massey.cs.gpl4jung.xml");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			Query q= (Query)unmarshaller.unmarshal(source);
						
			for (Object o:q.getVertexOrPathOrEdge()) {
				//getting roles (vertex id) from query
				if (o instanceof Query.Vertex) {
					Query.Vertex v = (Query.Vertex)o;
					v_roles.add(v.id); 
					//getting simple vertex property constraints
					for(Iterator itr=v.getProperty().iterator(); itr.hasNext();){
						Query.Vertex.Property p = (Query.Vertex.Property) itr.next();
						PropertyTerm term1 = new PropertyTerm(p.getKey());
						ValueTerm term2 = new ValueTerm(p.getValue());
						SimplePropertyConstraint<Vertex> pc = new SimplePropertyConstraint<Vertex>();
						pc.setOwner(v.id);
						pc.setTerms(term1, term2);
						constraints.add(pc);
					}
					//getting complex vertex property constraints (using or)
					
					Query.Vertex.Or orProp = v.getOr();
					List<PropertyConstraint> propCon = new ArrayList<PropertyConstraint>();
					PropertyConstraintDisjunction ComplexPropConstraint = new PropertyConstraintDisjunction();
					if(orProp!=null){
						for(Iterator itr = orProp.getProperty().iterator();itr.hasNext();){
							Query.Vertex.Or.Property prp = (Query.Vertex.Or.Property) itr.next();
							PropertyTerm term1 = new PropertyTerm(prp.getKey());
							ValueTerm term2 = new ValueTerm(prp.getValue());
							SimplePropertyConstraint<Vertex> spc = new SimplePropertyConstraint<Vertex>();
							spc.setOwner(v.getId());
							spc.setTerms(term1, term2);
							//Operator op = Operator.getInstance("matches");
							//spc.setOperator(op);
							propCon.add(spc);
						}
						ComplexPropConstraint.setParts(propCon);
						ComplexPropConstraint.setOwner(v.getId());
						constraints.add(ComplexPropConstraint);
						
					}
				}

				//getting path constraint from query
				else if (o instanceof Query.Path) {
					PathConstraint pathConstraint = new PathConstraint();
					Query.Path p = (Query.Path)o;
					//System.out.println("path from " + p.getFrom() + " to " + p.getTo());
					if(p.getMaxLength()!=null){
						pathConstraint.setMaxLength(p.getMaxLength());
					}
					
					pathConstraint.setSource(p.getFrom());
					pathConstraint.setTarget(p.getTo());
					//getting simple path property constraint
					Query.Path.Property pp = p.getProperty();
					if(pp!=null){
						PropertyTerm term1 = new PropertyTerm(pp.getKey());
						ValueTerm term2 = new ValueTerm(pp.getValue());
						SimplePropertyConstraint<Edge> pathPropConstraint = new SimplePropertyConstraint<Edge>();
						pathPropConstraint.setOwner(pathConstraint.getID());
						pathPropConstraint.setTerms(term1, term2);
						//pathConstraint.setPredicate("");
						pathConstraint.setEdgePropertyConstraint(pathPropConstraint);
					}
					//getting complex path property constraints (using OR operator)
					Query.Path.Or orProp = p.getOr();
					List<PropertyConstraint> propCon = new ArrayList<PropertyConstraint>();
					PropertyConstraintDisjunction ComplexPropConstraint = new PropertyConstraintDisjunction();
					if(orProp!=null){
						for(Iterator itr = orProp.getProperty().iterator();itr.hasNext();){
							Query.Path.Or.Property prp = (Query.Path.Or.Property) itr.next();
							PropertyTerm term1 = new PropertyTerm(prp.getKey());
							ValueTerm term2 = new ValueTerm(prp.getValue());
							SimplePropertyConstraint<Vertex> pc = new SimplePropertyConstraint<Vertex>();
							pc.setOwner(pathConstraint.getID());
							pc.setTerms(term1, term2);
							propCon.add(pc);
						}
						ComplexPropConstraint.setParts(propCon);
						pathConstraint.setEdgePropertyConstraint(ComplexPropConstraint);
					}
					constraints.add(pathConstraint);
				}
				else if (o instanceof Query.Edge){
					EdgeConstraint edgeConstraint = new EdgeConstraint();
					Query.Edge e = (Query.Edge) o;
					edgeConstraint.setSource(e.getFrom());
					edgeConstraint.setTarget(e.getTo());
					//getting edge properties
					Query.Edge.Property ee = e.getProperty();
					if(ee!=null){
						PropertyTerm term1 = new PropertyTerm(ee.getKey());
						ValueTerm term2 = new ValueTerm(ee.getValue());
						SimplePropertyConstraint<Edge> edgePropConstraint = new SimplePropertyConstraint<Edge>();
						edgePropConstraint.setTerms(term1,term2);
						edgePropConstraint.setOwner(edgeConstraint.getID());
						edgeConstraint.setEdgePropertyConstraint(edgePropConstraint);
					}
					constraints.add(edgeConstraint);
				}
				//TODO: to be comepleted
				else if (o instanceof Query.Condition) {
					Query.Condition c = (Query.Condition)o;
					ComplexPropertyConstraint p1 = new PropertyConstraintConjunction();
					SimplePropertyConstraint part1 = new SimplePropertyConstraint();
					SimplePropertyConstraint part2 = new SimplePropertyConstraint();
					List<PropertyConstraint> parts = new ArrayList<PropertyConstraint>();
					Operator op = Operator.getInstance("=");
					PropertyConstraintConjunction complexProp = new PropertyConstraintConjunction();
					//getting condition attributes and mapping to property terms
					Query.Condition.Attribute a = c.getAttribute().get(0);
					part1.setOperator(op);
					PropertyTerm term = new PropertyTerm(a.getKey());
					part1.setOwner(a.getVertex());
					part1.setTerms(term);
					parts.add(part1);
					//getting 2nd part
					Query.Condition.Attribute a1 = c.getAttribute().get(1);
					part2.setOperator(op);
					PropertyTerm term2 = new PropertyTerm("namespace");
					part2.setOwner(a1.getVertex());
					part2.setTerms(term2);
					parts.add(part2);
					complexProp.setParts(parts);
					complexProp.setOwner(a.getVertex());
					constraints.add(complexProp);
					
				}
				//TODO: to be completed
				else if (o instanceof Query.Not){
					Query.Not n = (Not) o;
					Query.Not.Condition c = n.getCondition();
					SimplePropertyConstraint part1 = new SimplePropertyConstraint();
					SimplePropertyConstraint part2 = new SimplePropertyConstraint();
					List<PropertyConstraint> parts = new ArrayList<PropertyConstraint>();
					Operator op = Operator.getInstance("=");
					PropertyConstraintConjunction complexProp = new PropertyConstraintConjunction();
					//getting condition attributes and mapping to property terms
					Query.Not.Condition.Attribute attr = c.getAttribute().get(0);
					part1.setOperator(op);
					PropertyTerm term = new PropertyTerm("namespace");
					part1.setOwner(attr.getVertex());
					part1.setTerms(term);
					parts.add(part1);
					//getting 2nd part
					Query.Not.Condition.Attribute a1 = c.getAttribute().get(1);
					part2.setOperator(op);
					PropertyTerm term2 = new PropertyTerm("namespace");
					part2.setOwner(a1.getVertex());
					part2.setTerms(term2);
					parts.add(part2);
					complexProp.setParts(parts);
					complexProp.setOwner(attr.getVertex());
					NegatedPropertyConstraint negPropConstraint = new NegatedPropertyConstraint();
					negPropConstraint.setPart(complexProp);
					negPropConstraint.setOwner("class1");
					constraints.add(negPropConstraint);
				}
				else if (o instanceof Query.ExistsNot){
					
				}
					
				
			}
			
			return motif;
		
		} catch (JAXBException e) {
			throw new MotifReaderException("exception reading motif from xml",e);
		}	
				
	}
}
