package nz.ac.massey.cs.utils.odem2graphml;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
/**
 * batch script to convert odem files to graphml files
 * @author jens dietrich
 */
public class ConversionScript {
	static {
		// only necessary when running inside massey
		System.getProperties().put( "proxySet", "true" );
		System.getProperties().put( "proxyHost", "tur-cache" );
		System.getProperties().put( "proxyPort", "8080" );
	}
	
	public static void main(String[] args) throws Exception {
		String INPUT = "odem-input/";
		String OUTPUT = "graphml-output/";
		File inputFolder = new File(INPUT);
		File[] files = inputFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getAbsolutePath().endsWith(".odem");
			}});
		System.out.println(""+files.length+" files will be converted");
		int counter = 0;
		Odem2GraphML odem2graphml = new Odem2GraphML();
		Reader reader = null;
		Writer writer = null;
		
		for (File in:files) {
			counter = counter+1;
			// enable for debugging:
			// if (counter>3) break;
			String resultFileName = in.getName().substring(0, in.getName().length()-5)+".graphml";
			File out = new File(OUTPUT+resultFileName);

			try {
				System.out.print("converting ");
				System.out.print(counter);
				System.out.print('/');
				System.out.print(files.length);
				System.out.print(": ");
				System.out.print(in.getName());
				System.out.print(" -> ");
				System.out.println(out.getName());
				
				reader = new FileReader(in);
				writer = new FileWriter(out);
				odem2graphml.convert(reader, writer);
				
				System.out.println("success");

			}
			catch (Exception x) {
				System.out.println("failed");
				x.printStackTrace();
			}
			finally {
				odem2graphml.reset();
				System.gc();
				try {reader.close();}
				catch (Exception x){}
				try {writer.close();}
				catch (Exception x){}
			}
		
		}
		System.exit(0);
	}
}
