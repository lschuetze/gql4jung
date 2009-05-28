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
	private String expression = null;
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
