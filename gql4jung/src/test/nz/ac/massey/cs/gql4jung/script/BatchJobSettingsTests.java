/**
 * Copyright 2009 Jens Dietrich, Heung Bae Jeon Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package test.nz.ac.massey.cs.gql4jung.script;

import static org.junit.Assert.*;
import java.io.File;
import nz.ac.massey.cs.gql4jung.script.BatchJobSettings;
import org.junit.Test;

/**
 * Test cases to check parameter parsing of batch job settings.
 * @author jens dietrich
 */
public class BatchJobSettingsTests {
	@Test
	public void testData1() throws Exception {
		String [] args = {"-data","exampledata/ant.jar.graphml"};
		BatchJobSettings settings = new BatchJobSettings(args);
		assertEquals(new File("exampledata/ant.jar.graphml"),settings.getDataSource());
		assertFalse(settings.getDataSource().isDirectory());
		assertFalse(settings.isDataIsFolder());
		assertFalse(settings.isDataIsRecursiveFolder());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testData2() throws Exception {
		String [] args = {"-data","exampledata/doesnotexist.graphml"};
		BatchJobSettings settings = new BatchJobSettings(args);
	}
}
