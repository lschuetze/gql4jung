/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.io;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.util.QueryResults;
import nz.ac.massey.cs.gql4jung.util.QueryResults.Cursor;

/**
 * Utilit to export results to CVS.
 * @author jens
 *
 */
public class QueryResultsExporter2CSV {
	public final static String SEP = ",";
	public void export(QueryResults results,File target) throws IOException {
		File folder = target.getParentFile();
		if (folder!=null && !folder.exists()) folder.mkdir();
		PrintStream out = new PrintStream(new FileOutputStream(target)); 
		List<String> roles = null;
		Iterator<Map.Entry<Cursor,MotifInstance>> iter = results.iterator();
		
		while (iter.hasNext()) {
			Map.Entry<Cursor,MotifInstance> next = iter.next();
			Cursor cursor = next.getKey();
			MotifInstance instance = next.getValue();
			if (roles==null) {
				Motif motif = instance.getMotif();
				roles = motif.getRoles();
				// print header
				out.print("instance");
				out.print(SEP);
				out.print("variant");
				for (String role:roles) {
					out.print(SEP);
					out.print(role);
				}
				out.println();
			}
			// print values
			out.print(1+cursor.major);
			out.print(SEP);
			out.print(1+cursor.minor);
			for (String role:roles) {
				out.print(SEP);
				Vertex vertex = instance.getVertex(role);
				out.print(vertex.getNamespace());
				out.print('.');
				out.print(vertex.getName());
			}
			out.println();
		}
		out.close();

	}
}
