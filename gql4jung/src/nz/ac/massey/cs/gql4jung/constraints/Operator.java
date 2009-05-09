/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung.constraints;

import java.util.StringTokenizer;
import java.util.regex.Pattern;



/**
 * Definition of operators.  
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

public abstract class Operator {
	
	static class EQ extends Operator { 
		public boolean compare(Object o1, Object o2) { 
			return o1==null?o2==null:o1.equals(o2); 
		} 
		public String getName() {
			return "="; 
		}
	};
	static class NEQ extends Operator { 
		public boolean compare(Object o1,Object o2) { 
			return o1==null?o2!=null:!o1.equals(o2); 
		} 
		public String getName() { 
			return "!="; 
		} 
	}; 
	static class IN extends Operator { 
		public boolean compare(Object o1,Object o2) { 
			if (o1==null) return false; 
			if (o2==null) return false; 
			String e = o1.toString(); 
			for (StringTokenizer tok=new StringTokenizer(o2.toString(),",");tok.hasMoreTokens();) { 
				if (e.equals(tok.nextToken().trim())) { 
					return true; 
				} 
			} 
			return false; 
		} 
		public String getName() { 
			return "IN"; 
		} 
	}; 
	static class REGEX extends Operator {
		public boolean compare(Object o1,Object o2) { 
			if (!(o1 instanceof String)) { 
				throw new IllegalArgumentException("the first parameter must be a string"); 
			} 
			if (!(o2 instanceof String)) { 
				throw new IllegalArgumentException("the second parameter must be a string"); 
			} 
			return Pattern.matches((String)o2,(String)o1);  
		} 
		public String getName() { 
			return "matches"; 
		} 
	};  
	static class LT extends Operator { 
		public boolean compare(Object o1,Object o2)  { 
			double d1 = toDouble(o1); 
			double d2 = toDouble(o2); 
			return d1<d2; 
		} 
		public String getName() { 
			return "lt"; 
		} 
	}; 
	static class LTE extends Operator { 
		public boolean compare(Object o1,Object o2){ 
			double d1 = toDouble(o1); 
			double d2 = toDouble(o2); 
			return d1<=d2; 
		} 
		public String getName() { 
			return "lte"; 
		} 
	}; 
	static class GT extends Operator { 
		public boolean compare(Object o1,Object o2) { 
			double d1 = toDouble(o1); 
			double d2 = toDouble(o2);
			return d1>d2; 
			} 
		public String getName() {
			return "gt"; 
		} 
	}; 
	static class GTE extends Operator { 
		public boolean compare(Object o1,Object o2) { 
			double d1 = toDouble(o1); 
			double d2 = toDouble(o2); 
			return d1>=d2; 
		} 
		public String getName() { 
			return "gte"; 
		} 
	}; 
	public static Operator[] INSTANCES = { new EQ(),new NEQ(),new IN(), new REGEX(), new LT(), new GT(), new LTE(), new GTE()  };  
	public static Operator getInstance(String name) { 
		for (Operator o:INSTANCES) { 
			if (o.getName().equalsIgnoreCase(name)) { 
				return o; 
			} 
		} 
		return null; 
	}   
	public abstract boolean compare(Object o1,Object o2); 
	public abstract String getName();  
	private static double toDouble(Object obj){
		if (obj instanceof String) { 
			return Double.parseDouble((String) obj); 
			} 
		else if (obj instanceof Double) { 
			return (Double)obj; 
			} 
		else if (obj instanceof Integer) { 
			return ((Integer)obj).doubleValue(); 
			} 
		else throw new IllegalArgumentException("double parameter expected here"); 
	}  
  
	public String toString() {
		return this.getName();
	}   
	

}