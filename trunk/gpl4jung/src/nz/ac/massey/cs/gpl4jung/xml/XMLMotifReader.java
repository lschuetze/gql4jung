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

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.uci.ics.jung.io.GraphMLFile;

import nz.ac.massey.cs.gpl4jung.Motif;
import nz.ac.massey.cs.gpl4jung.MotifReader;
import nz.ac.massey.cs.gpl4jung.MotifReaderException;

public class XMLMotifReader implements MotifReader {

	@Override
	public Motif read(InputStream source) throws MotifReaderException {
		try {
		JAXBContext jc= JAXBContext.newInstance("nz.ac.massey.cs.gpl4jung.xml");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Query q= (Query)unmarshaller.unmarshal(source);
		System.out.println(q);
		/*for (Object o:q.getVertexOrPathOrCondition()) {
			if (o instanceof Query.Path) {
				Query.Path p = (Query.Path)o;
				System.out.println("path from " + p.getFrom() + " to " + p.getTo());
			}
		}*/
		
		} catch (JAXBException e) {
			e.printStackTrace();
		}	
		return null;	//what to return eith whole query q or list of objects
		
	}
}
