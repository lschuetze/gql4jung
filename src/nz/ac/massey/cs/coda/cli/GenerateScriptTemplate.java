/**
 * Copyright 2010 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.codeanalysis.cli;

import java.io.File;
import java.io.PrintStream;

/**
 * Simple utility to generate a script template and print in to System.out. This is in particular helpful to get parameters
 * such as classpath settings right. 
 * @author jens dietrich
 *
 */
public class GenerateScriptTemplate {
	public static void main(String[] args) {
		PrintStream out = System.out;
		
		String sep = System.getProperty("path.separator");
		out.print("java -Xmx1024m "); // plenty of memory is needed to parse large files, running queries is less demanding!
		out.print("-cp bin");
		
		
		// libs
		for (File lib:new File("lib").listFiles()) {
			if (!lib.getName().startsWith(".")) {
				out.print(sep);
				out.print("lib");
				out.print("/");
				out.print(lib.getName());
			}
		}
		out.print(" ");
		
		// main class
		out.print(Run.class.getName());
		
		// some parameters - set to first query and to first data file found
		out.print(" -input exampledata/log4j-1.2.15.jar");
		out.print(" -motif queries/awd.xml");
		out.print(" -threads 2");
		out.print(" -variants");
		
		out.println();
		out.println(Runtime.getRuntime().availableProcessors());
		
		
		
	}
}
