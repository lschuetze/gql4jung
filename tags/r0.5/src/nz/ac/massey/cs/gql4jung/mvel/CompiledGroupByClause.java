/**
 * Copyright 2009 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.gql4jung.mvel;

import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;
import nz.ac.massey.cs.gql4jung.GraphElement;
import nz.ac.massey.cs.gql4jung.GroupByClause;
import nz.ac.massey.cs.gql4jung.Vertex;

/**
 * Group by clause, based on a compiled mvel expression.
 * @author jens dietrich
 */
public class CompiledGroupByClause implements GroupByClause{
	public String expression = null;
	private String role = null;
	private CompiledExpression compiledExpression = null;
	public CompiledGroupByClause(String expression) {
		this.expression = expression;
		ParserContext ctx = new ParserContext();
		// compile
		this.compiledExpression = new ExpressionCompiler(expression).compile(ctx);
		if (ctx.getInputs().size()!=1) throw new IllegalArgumentException("Expressions should only have one input");
		role = ctx.getInputs().keySet().iterator().next();
	}
	public String getRole() {
		return role;
	}
	public Object getGroup(Vertex v) {
		if (expression.equals(role)) return v;
		Map<String,GraphElement> map = new HashMap<String,GraphElement>(1);
		map.put(role,v);
		Object result = MVEL.executeExpression(this.compiledExpression,map);
		return result;
	}
	public String getExpression() {
		return expression;
	}
}
