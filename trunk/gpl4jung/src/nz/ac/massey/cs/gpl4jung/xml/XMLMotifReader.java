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
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import nz.ac.massey.cs.gpl4jung.*;
import nz.ac.massey.cs.gpl4jung.constraints.PathConstraint;

public class XMLMotifReader implements MotifReader {

	@Override
	public Motif read(InputStream source) throws MotifReaderException {
		try {
			DefaultMotif motif = new DefaultMotif();
			List<String> v_roles = new ArrayList<String>();
			List<LinkConstraint> constraints = new ArrayList<LinkConstraint>();
			motif.setConstraints(constraints);
			
			
			//unmarshalling xml query
			JAXBContext jc= JAXBContext.newInstance("nz.ac.massey.cs.gpl4jung.xml");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			Query q= (Query)unmarshaller.unmarshal(source);
			//System.out.println(q);
			
			//getting path constraint from query
			for (Object o:q.getVertexOrPathOrCondition()) {
				if (o instanceof Query.Path) {
					PathConstraint pc = new PathConstraint();
					Query.Path p = (Query.Path)o;
					//System.out.println("path from " + p.getFrom() + " to " + p.getTo());
					pc.setMinLength(p.getMinLength());
					pc.setMaxLength(p.getMaxLength());
					constraints.add(pc);
				}
				else if (o instanceof Query.Condition) {
					Query.Condition p = (Query.Condition)o;
					
				}
				// TODO: complex conditions, edge constraints ..
			}
			
			
			//getting roles (vertex id) from query
			for (Object o:q.getVertexOrPathOrCondition()) {
				if (o instanceof Query.Vertex) {
					Query.Vertex v = (Query.Vertex)o;
					v_roles.add(v.id);
					}
			}

			motif.setRoles(v_roles);
			motif.setConstraints(constraints);
			return motif;
		
		} catch (JAXBException e) {
			throw new MotifReaderException("exception reading motif from xml",e);
		}	
				
	}
}
