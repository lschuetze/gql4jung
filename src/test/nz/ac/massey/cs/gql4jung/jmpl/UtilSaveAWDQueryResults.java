/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package test.nz.ac.massey.cs.gql4jung.jmpl;

import java.io.File;
import nz.ac.massey.cs.gql4jung.Edge;
import nz.ac.massey.cs.gql4jung.GQL;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.io.QueryResultsExporter2CSV;
import nz.ac.massey.cs.gql4jung.jmpl.GQLImpl;
import nz.ac.massey.cs.gql4jung.util.QueryResults;
import edu.uci.ics.jung.graph.DirectedGraph;
/**
 * Utility to save query results for investigation.
 * @author jens dietrich
 */
public class UtilSaveAWDQueryResults {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DirectedGraph<Vertex,Edge> g = Tests.loadGraph("ant.jar.graphml");
		Motif m = Tests.loadQuery("awd.xml");
		QueryResults coll = new QueryResults();
		GQL engine = new GQLImpl();
		long t1 = System.currentTimeMillis();
		engine.query(g,m,coll);
		long t2 = System.currentTimeMillis();
		QueryResultsExporter2CSV exporter = new QueryResultsExporter2CSV();
		File file = new File("awd-results.txt");
		exporter.export(coll,file);
		System.out.println("query results exported to " + file.getAbsolutePath());

	}

}
