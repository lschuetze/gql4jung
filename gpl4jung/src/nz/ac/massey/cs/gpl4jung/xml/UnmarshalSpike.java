package nz.ac.massey.cs.gpl4jung.xml;

import java.io.File;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.uci.ics.jung.io.GraphMLFile;


public class UnmarshalSpike {

	/**
	 * @param args
	 * @throws JAXBException 
	 */
	public static void main(String[] args) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("nz.ac.massey.cs.gpl4jung.xml");		
		//GraphMLFile input = new GraphMLFile();

		/*Unmarshaller unmarshaller = jc.createUnmarshaller();
		Query q= (Query)unmarshaller.unmarshal(new File( "xml/query1.xml"));
		System.out.println(q);
		for (Object o:q.getVertexOrPathOrCondition()) {
			if (o instanceof Query.Path) {
				Query.Path p = (Query.Path)o;
				System.out.println("path from " + p.getFrom() + " to " + p.getTo());
			}
		}*/
		
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Query q= (Query)unmarshaller.unmarshal(new File( "xml/query1.xml"));
		System.out.println(q);
		for (Object o:q.getVertexOrPathOrCondition()) {
			if (o instanceof Query.Vertex) {
				Query.Vertex v = (Query.Vertex)o;
				System.out.println("vertex property: " + v.getProperty() + " Vertex Type: " + v.getType());
			}
		}

	}

}
