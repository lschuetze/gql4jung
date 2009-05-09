package test.nz.ac.massey.cs.gql4jung.gql;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ArrangeData {

	/** moves graphml project file into its own folder. 
	 * Also creates some subfolders for every project
	 * @param args
	 * @throws IOException 
	 * @auther Ali
	 */
	public static void main(String[] args) throws IOException {
		String INPUT = "Data/";
		File inputFolder = new File(INPUT);
		File[] files = inputFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getAbsolutePath().endsWith(".graphml");
			}});
		System.out.println(""+files.length+" files will be copied");
		int counter = 0;
		for(File in:files){
			counter = counter+1;
			System.out.print("copying ");
			System.out.print(counter);
			System.out.print('/');
			System.out.print(files.length);
			System.out.print(": ");
			System.out.print(in.getName());
			
			String folderName = in.getName().substring(0,in.getName().length()-8);
			new File (inputFolder,folderName).mkdir();
			String pathName = INPUT+folderName+"/";
			new File (pathName+"abstraction_coupling").mkdir();
			new File (pathName+"circular_dependency").mkdir();
			new File (pathName+"db2ui_dependency").mkdir();
			new File (pathName+"multiple_clusters").mkdir();
			File dest = new File(pathName+in.getName());
			FileInputStream fis = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(dest);
			FileChannel srcChannel = fis.getChannel();
			FileChannel destChannel = fos.getChannel();
			srcChannel.transferTo(0, in.length(), destChannel);
			srcChannel.close();
			destChannel.close();
			fis.close();
			fos.close();
			in.delete();
			System.out.println();
			System.out.println("success");
		}
		
	}

}
