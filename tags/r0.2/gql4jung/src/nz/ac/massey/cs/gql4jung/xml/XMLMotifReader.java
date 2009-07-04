/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.gql4jung.*;
import nz.ac.massey.cs.gql4jung.constraints.EdgeConstraint;
import nz.ac.massey.cs.gql4jung.constraints.NegatedPropertyConstraint;
import nz.ac.massey.cs.gql4jung.constraints.Operator;
import nz.ac.massey.cs.gql4jung.constraints.PathConstraint;
import nz.ac.massey.cs.gql4jung.constraints.PropertyConstraintConjunction;
import nz.ac.massey.cs.gql4jung.constraints.PropertyConstraintDisjunction;
import nz.ac.massey.cs.gql4jung.constraints.PropertyTerm;
import nz.ac.massey.cs.gql4jung.constraints.RolePropertyTerm;
import nz.ac.massey.cs.gql4jung.constraints.SimplePropertyConstraint;
import nz.ac.massey.cs.gql4jung.constraints.ValueTerm;
import nz.ac.massey.cs.gql4jung.xml.Query.Condition;
import nz.ac.massey.cs.gql4jung.xml.Query.ExistsNot;
import nz.ac.massey.cs.gql4jung.xml.Query.Groupby.Element;
import nz.ac.massey.cs.processors.ClusterProcessor;
import nz.ac.massey.cs.processors.Processor;

/**
 * Builds motifs from xml streams
 * @deprecated - to be replaces soon (jens)
 * @author ali
 */
public class XMLMotifReader implements MotifReader {

	@Override
	public Motif read(InputStream source) throws MotifReaderException {
		try {
			DefaultMotif motif = new DefaultMotif();
			List<String> v_roles = new ArrayList<String>();
			List<Constraint> constraints = new ArrayList<Constraint>();
			List<GroupByClause> groupByClauses = new ArrayList<GroupByClause>();
			List<Processor> graphProcessors = new ArrayList<Processor>();
			motif.setRoles(v_roles);
			motif.setConstraints(constraints);
			motif.setGroupByClauses(groupByClauses);
			motif.setGraphProcessor(graphProcessors);
			
			//unmarshalling xml query
			JAXBContext jc= JAXBContext.newInstance("nz.ac.massey.cs.gql4jung.xml");
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
					pathConstraint.setMaxLength(p.getMaxLength());
					pathConstraint.setMinLength(p.getMinLength());
					Query.Vertex from = (Query.Vertex) p.getFrom();
					Query.Vertex to = (Query.Vertex) p.getTo();
					pathConstraint.setSource(from.getId());
					pathConstraint.setTarget(to.getId());
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
					Query.Vertex from = (Query.Vertex) e.getFrom();
					Query.Vertex to = (Query.Vertex) e.getTo();
					edgeConstraint.setSource(from.getId());
					edgeConstraint.setTarget(to.getId());
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
				
				else if (o instanceof Query.Condition) {
					Query.Condition c = (Query.Condition)o;
					Constraint constraint = buildStandaloneConstraint(c);
					constraints.add(constraint);
					
				}
				else if (o instanceof Query.Not){
					Query.Not n = (Query.Not) o;
					Query.Not.Condition c1 = n.getCondition();
					NegatedPropertyConstraint negPropConstraint = new NegatedPropertyConstraint();
					PropertyConstraint c = this.buildStandaloneConstraint(c1);				
					negPropConstraint.setPart(c);
					constraints.add(negPropConstraint);
				}
				else if (o instanceof Query.ExistsNot){
					Query.ExistsNot cond = (ExistsNot) o;
					Query.ExistsNot.Vertex v = cond.getVertex();
					NegatedPropertyConstraint negPropConstraint = new NegatedPropertyConstraint();
					v_roles.add(v.getId());
					//getting vertex property
					Query.ExistsNot.Vertex.Property p = v.getProperty();
					PropertyTerm term1 = new PropertyTerm(p.getKey());
					ValueTerm term2 = new ValueTerm(p.getValue());
					SimplePropertyConstraint<Vertex> pc = new SimplePropertyConstraint<Vertex>();
					pc.setOwner(v.getId());
					pc.setTerms(term1, term2);
					negPropConstraint.setPart(pc);
					negPropConstraint.setOwner(v.getId());
					constraints.add(negPropConstraint);
				}
				else if (o instanceof Query.Graphprocessor){
					Query.Graphprocessor gp = (Query.Graphprocessor)o;
					ClusterProcessor cp= new ClusterProcessor(); 
					cp.setProcessorClass(gp.getClazz());
					graphProcessors.add(cp);
				}

				else if (o instanceof Query.Groupby){
					Query.Groupby groupBy = (Query.Groupby)o;
					GroupByClause groupByClause = new GroupByClause();
					List<Query.Groupby.Element> elements = groupBy.getElement();
					if(elements!=null){
						for(Iterator itr=elements.iterator();itr.hasNext();){
							Query.Groupby.Element e = (Element) itr.next();
							Query.Vertex role = (Query.Vertex)e.getVertex();
							groupByClause.setRole(role.getId());
							groupByClauses.add(groupByClause);
						}
					} 
					List<Query.Groupby.Vertex> vertexRoles = groupBy.getVertex();
					if(vertexRoles!=null){
						for(Iterator itr = vertexRoles.iterator();itr.hasNext();){
							Query.Groupby.Vertex v = (Query.Groupby.Vertex) itr.next();
							Query.Vertex role = (Query.Vertex) v.getId();
							groupByClause.setRole(role.getId());
							groupByClause.setProperty(v.getProperty());
							groupByClauses.add(groupByClause);
						}
					}
				}
				
			}
			
			return motif;
		
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new MotifReaderException("exception reading motif from xml",e);
		}	
				
	}

	private Constraint buildStandaloneConstraint(Condition c) throws MotifReaderException {
		SimplePropertyConstraint constraint = new SimplePropertyConstraint();
		if (!"equals".equals(c.getPredicate())) throw new MotifReaderException("the only opertator currently supported is equals");
		Operator op = Operator.getInstance("="); // TODO this is hardcoded here !!
		PropertyConstraintConjunction complexProp = new PropertyConstraintConjunction();
		//getting condition attributes and mapping to property terms
		if (c.getAttribute().size()!=2) throw new MotifReaderException("only two arguments in property condition comparing properties from different vertices are allowed");
		Query.Condition.Attribute a1 = c.getAttribute().get(0);
		Query.Condition.Attribute a2 = c.getAttribute().get(1);
		RolePropertyTerm t1 = new RolePropertyTerm(a1.getVertex(),a1.getKey());
		RolePropertyTerm t2 = new RolePropertyTerm(a2.getVertex(),a2.getKey());
		constraint.setOperator(op);
		constraint.setTerms(t1,t2);
		return constraint;
	}
	// TODO: this is a clone of the overloaded method - this must be fixed in the schema !!
	// result of dirty copy and paste in the schema - there is more of this !!
	private PropertyConstraint buildStandaloneConstraint(Query.Not.Condition c) throws MotifReaderException {
		SimplePropertyConstraint constraint = new SimplePropertyConstraint();
		if (!"equals".equals(c.getPredicate())) throw new MotifReaderException("the only opertator currently supported is equals");
		Operator op = Operator.getInstance("="); // TODO this is hardcoded here !!
		PropertyConstraintConjunction complexProp = new PropertyConstraintConjunction();
		//getting condition attributes and mapping to property terms
		if (c.getAttribute().size()!=2) throw new MotifReaderException("only two arguments in property condition comparing properties from different vertices are allowed");
		Query.Not.Condition.Attribute a1 = c.getAttribute().get(0);
		Query.Not.Condition.Attribute a2 = c.getAttribute().get(1);
		RolePropertyTerm t1 = new RolePropertyTerm(a1.getVertex(),a1.getKey());
		RolePropertyTerm t2 = new RolePropertyTerm(a2.getVertex(),a2.getKey());
		constraint.setOperator(op);
		constraint.setTerms(t1,t2);
		return constraint;
	}
}