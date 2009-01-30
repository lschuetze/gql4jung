/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gpl4jung.constraints;


/**
 * Definition of operators.  
 * TODO complete
 * The query parser must map operators to these constants, for instance, in an XML based language it
 * must map URIs defined in http://www.w3.org/TR/xpath-functions/ to instances.
 * @author jens.dietrich@gmail.com
 */
//public enum Operators  {
//	EQUALS,
//	MATCHES,
//	LESS_THAN,
//	GREATER_THAN,
//	LESS_THAN_OR_EQUALS,
//	GREATER_THAN_OR_EQUALS
//}

public abstract class Operators {
	static class EQ extends Operators { 
		public boolean check(Object... values) { 
			assert(values.length==2);
			return values[0]==null?values[1]==null:values[0].equals(values[1]); 
		} 
		public String getName() {
			return "="; 
		}
	};
	public static Operators[] INSTANCES = { new EQ()};  
	public static Operators getInstance(String name) { 
		for (Operators o:INSTANCES) { 
			if (o.getName().equalsIgnoreCase(name)) { 
				return o; 
			} 
		} 
		return null; 
	}   
	public abstract boolean check(Object... values); 
	public abstract String getName();  
}