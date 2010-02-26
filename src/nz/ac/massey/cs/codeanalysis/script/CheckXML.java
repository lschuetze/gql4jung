package nz.ac.massey.cs.codeanalysis.script;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class CheckXML {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception  {
		File folder = new File("batch/graphml");
		int failedCount = 0;
		int successCount = 0;
		for (File f:folder.listFiles()) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			try {
				db.parse(f);
				System.out.println("parsing succeeded for " + f);
				successCount++;
			}
			catch (Exception x) {
				System.err.println("parsing failed for " + f);
				failedCount++;
			}
		}
		System.out.println("Check done: ");
		System.out.println("Checked files: " + folder.listFiles().length);
		System.out.println("Success: " + successCount);
		System.out.println("Failed: " + failedCount);
	}

}
