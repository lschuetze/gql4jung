package nz.ac.massey.cs.gql4jung.browser;

import java.awt.Window;
import java.io.IOException;

import nz.ac.massey.cs.codeanalysis.TypeNode;
import nz.ac.massey.cs.codeanalysis.TypeReference;
import nz.ac.massey.cs.gql4jung.util.QueryResults;

public interface ResultExportAction {
	public String getName();
	public String getDescription();
	public void exportResults(QueryResults<TypeNode,TypeReference> results,Window context) throws IOException ;
}
