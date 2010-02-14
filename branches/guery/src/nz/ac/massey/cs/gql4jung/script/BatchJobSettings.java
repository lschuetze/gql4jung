/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.script;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
/**
 * Analysis job parameters. Can be initialised from parameter strings. 
 * @author jens dietrich
 */
public class BatchJobSettings {
	private File dataSource = null;
	private File querySource = null;
	private File output = null;
	private boolean dataIsFolder = false;
	private boolean dataIsRecursiveFolder = false;
	
	// initialise an instance from runtime parameters
	public BatchJobSettings(String[] args) {
		super();
		// TODO
	}
	
	public File getDataSource() {
		return dataSource;
	}
	public File getQuerySource() {
		return querySource;
	}
	public File getOutput() {
		return output;
	}
	public boolean isDataIsFolder() {
		return dataIsFolder;
	}
	public boolean isDataIsRecursiveFolder() {
		return dataIsRecursiveFolder;
	}
	public void setDataSource(File dataSource) {
		this.dataSource = dataSource;
	}
	public void setQuerySource(File querySource) {
		this.querySource = querySource;
	}
	public void setOutput(File output) {
		this.output = output;
	}
	public void setDataIsFolder(boolean dataIsFolder) {
		this.dataIsFolder = dataIsFolder;
	}
	public void setDataIsRecursiveFolder(boolean dataIsRecursiveFolder) {
		this.dataIsRecursiveFolder = dataIsRecursiveFolder;
	}
	/**
	 * returns a collection of data files, this collection is 
	 * a singleton, if the data source is one file
	 * a list of files if the data source is a folder
	 * @return
	 */
	public Collection<File> getDataFiles() {
		if (!this.isDataIsFolder()) {
			Collection<File> coll = new ArrayList<File>(1);
			coll.add(dataSource);
			return coll;
		}
		else if (!this.isDataIsRecursiveFolder()) {
			Collection<File> coll = new ArrayList<File>();
			for (File f:this.dataSource.listFiles()) {
				if (isData(f)) {
					coll.add(f);
				}
			}
			return coll;
		}
		else {
			
		}
		// returns data files
		return null;
	}
	private boolean isData(File f) {
		return false;
	}

	public Collection<File> getQueryFiles() {
		return null;
	}

}
