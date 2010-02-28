/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.codeanalysis.io;

import java.io.Reader;
import org.jdom.Element;
import edu.uci.ics.jung.io.GraphIOException;
import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;

/**
 * Read graphml files representing software dependency graphs.
 * @author jens dietrich
 */
public class GraphMLReader extends nz.ac.massey.cs.gql4jung.io.GraphMLReader<TypeNode,TypeReference> {

	public GraphMLReader(Reader reader) {
		super(reader);
	}

	protected TypeReference createNewEdge() {
		return new TypeReference();
	}
	protected TypeNode createNewVertex() {
		return new TypeNode();
	}
	
	protected void readAttributes(TypeReference edge,Element e) throws GraphIOException {	
		String type = e.getAttributeValue("type");
		if (type==null) {
			
			throw new GraphIOException("Type attribute missing in edge " + e.getAttributeValue("id"));
		}
		edge.setType(type);
	}
	
	protected void readAttributes(TypeNode v,Element e) throws GraphIOException {
		v.setId(e.getAttributeValue("id"));
		v.setName(e.getAttributeValue("name"));
		v.setAbstract("true".equals(e.getAttributeValue("isAbstract")));
		v.setNamespace(e.getAttributeValue("namespace"));
		v.setType(e.getAttributeValue("type"));
		v.setCluster(e.getAttributeValue("cluster"));
		v.setContainer(e.getAttributeValue("container"));
	}


}
