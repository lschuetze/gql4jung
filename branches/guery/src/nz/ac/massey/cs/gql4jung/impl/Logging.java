/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.impl;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Logging {
	static Logger LOG_GQL = Logger.getLogger(GQLImpl.class);
	static Logger LOG_BIND = Logger.getLogger(""+Controller.class+":binding");
	static Logger LOG_BACKJUMP = Logger.getLogger(""+Controller.class+":backjump");
	static Logger LOG_INST = Logger.getLogger(MotifInstanceImpl.class);
	static Logger LOG_SCHED = Logger.getLogger(ConstraintScheduler.class);
	
	static {
		BasicConfigurator.configure();
	}
	Logging() {
		super();
		
		// debugging
		Level level = Level.WARN;
		//Level level = Level.DEBUG;
		// runtime
		LOG_GQL.setLevel(level);
		LOG_BIND.setLevel(level);
		LOG_BACKJUMP.setLevel(level);
		LOG_INST.setLevel(level);
		LOG_SCHED.setLevel(level);
	}
}
