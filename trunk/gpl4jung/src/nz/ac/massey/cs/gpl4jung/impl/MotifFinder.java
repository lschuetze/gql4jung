package nz.ac.massey.cs.gpl4jung.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import nz.ac.massey.cs.gpl4jung.Constraint;
import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.xml.Query;
import nz.ac.massey.cs.gpl4jung.xml.Query.Vertex;

public class MotifFinder implements Motif {

	@Override
	public List<Constraint> getConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getRoles()throws JAXBException {
		List<String> roles = new ArrayList();
 		JAXBContext jc = JAXBContext.newInstance("nz.ac.massey.cs.gpl4jung.xml");		
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Query q= (Query)unmarshaller.unmarshal(new File( "xml/query1.xml"));
		System.out.println(q);
		for (Object o:q.getVertexOrPathOrCondition()) {
			if (o instanceof Query.Vertex) {
				roles.add((String) o);
			}
		}
		return roles;
	}

	@Override
	public void setConstraints(List<Constraint> constraints) {
		// TODO Auto-generated method stub

	}

}
