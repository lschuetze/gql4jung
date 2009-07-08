/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.gql4jung;

/**
 * Constraint of edges or vertices.
 * @author jens dietrich
 */
import java.util.List;
import java.util.Map;
public interface PropertyConstraint extends Constraint {

	public List<String> getRoles() ;
	public String getFirstRole() ;
	public String getExpression();
	public boolean check(Object edgeOrVertexOrPath);
	public boolean check(Map<String,Object> bindings);
	public boolean isSingleRole();

}