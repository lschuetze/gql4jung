package nz.ac.massey.cs.gql4jung.browser.actions;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import nz.ac.massey.cs.gql4jung.browser.ResultExportAction;
import nz.ac.massey.cs.gql4jung.io.QueryResultsExporter2CSV;
import nz.ac.massey.cs.gql4jung.util.QueryResults;

public class CSVResultExportAction implements ResultExportAction {
	private static Logger LOG = Logger.getLogger(CSVResultExportAction.class);
	@Override
	public void exportResults(QueryResults results,Window context) throws IOException {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(context);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getAbsolutePath().endsWith(".csv");
			}
			@Override
			public String getDescription() {
				return "csv files";
			}			
		};
		fc.setFileFilter(filter);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            QueryResultsExporter2CSV exporter = new QueryResultsExporter2CSV();
			exporter.export(results,file);
			LOG.info("results exported to " + file.getAbsolutePath());
			JOptionPane.showMessageDialog(context,"Results have been exported to\n" + file.getAbsolutePath());
        }
		
	}
	
	public String getName() {
		return "export as CSV";
	}
	public String getDescription() {
		return "export results to comma separated files (spreadsheets)";
	}

}
