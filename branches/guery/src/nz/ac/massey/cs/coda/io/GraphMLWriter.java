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

import java.io.PrintWriter;
import java.io.Writer;
import edu.uci.ics.jung.io.GraphIOException;
import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;

/**
 * Simple utility to write graphml files. 
 * memory.
 * @author jens dietrich
 */
public class GraphMLWriter extends nz.ac.massey.cs.gql4jung.io.GraphMLWriter<TypeNode,TypeReference> {

	public GraphMLWriter(Writer writer) {
		super(writer);
	}

	@Override
	protected void writeAttributes(PrintWriter out, TypeReference e) throws GraphIOException {
		printAttr(out,"type",e.getType());
	}

	@Override
	protected void writeAttributes(PrintWriter out, TypeNode v) throws GraphIOException {
		printAttr(out,"container",v.getContainer());
		printAttr(out,"namespace",v.getNamespace());
		printAttr(out,"name",v.getName());
		printAttr(out,"cluster",v.getCluster());
		printAttr(out,"type",v.getType());
		printAttr(out,"isAbstract",String.valueOf(v.isAbstract()));		
	}


}
