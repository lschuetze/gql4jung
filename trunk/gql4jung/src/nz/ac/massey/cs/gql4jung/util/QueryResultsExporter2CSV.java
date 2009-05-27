package nz.ac.massey.cs.gql4jung.util;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import nz.ac.massey.cs.gql4jung.Motif;
import nz.ac.massey.cs.gql4jung.MotifInstance;
import nz.ac.massey.cs.gql4jung.Vertex;
import nz.ac.massey.cs.gql4jung.util.QueryResults.Cursor;

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
