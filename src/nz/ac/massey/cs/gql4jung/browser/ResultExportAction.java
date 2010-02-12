package nz.ac.massey.cs.gql4jung.browser;

import java.awt.Window;
import java.io.IOException;
import nz.ac.massey.cs.gql4jung.util.QueryResults;

public interface ResultExportAction {
	public String getName();
	public String getDescription();
	public void exportResults(QueryResults results,Window context) throws IOException ;
}
