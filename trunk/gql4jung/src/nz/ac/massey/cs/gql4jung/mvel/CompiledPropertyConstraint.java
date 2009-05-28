package nz.ac.massey.cs.gql4jung.mvel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nz.ac.massey.cs.gql4jung.GraphElement;
import nz.ac.massey.cs.gql4jung.PropertyConstraint;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;


/**
 * Property constraint defined in MVEL2.
 * @author jens dietrich
 * @see http://mvel.codehaus.org/
 */
public class CompiledPropertyConstraint implements PropertyConstraint  {
	private String expression = null;
	private CompiledExpression compiledExpression = null;
	private List<String> roleNames = new ArrayList<String>();

	public CompiledPropertyConstraint(String expression, Map<String,Class> roleTypes) {
		super();
		this.expression = expression;
		this.parseExpression(roleTypes);
	}

	private void parseExpression(Map<String, Class> roleTypes) {
		ParserContext ctx = new ParserContext();
		for (Map.Entry<String,Class> t:roleTypes.entrySet()) {
			if (!(GraphElement.class.isAssignableFrom(t.getValue()))) {
				throw new IllegalArgumentException("Illegal role type for role " + t.getKey()+", only vertices and edges are allowed here");
			}
		}
		// compile
		this.compiledExpression = new ExpressionCompiler(expression).compile(ctx);
		
		// check result type
		/*
		if (Boolean.class!=compiledExpression.getKnownEgressType()) {
			throw new IllegalArgumentException("Illegal result type for expression " + this.expression+", expected boolean but found " + compiledExpression.getKnownEgressType());
		}
		*/
		
		// gather input roles
		for (Object o:ctx.getInputs().keySet()) {
			this.roleNames.add((String)o);
		}
	}

	@Override
	public boolean check(GraphElement eov) {
		try {
			assert this.roleNames.size()==1;
			Map<String,GraphElement> map = new HashMap<String,GraphElement>(1);
			map.put(this.roleNames.get(0),eov);
			Object result = MVEL.executeExpression(this.compiledExpression,map);
			return (Boolean)result;
			//return MVEL.evalToBoolean(this.expression,eov);
		}
		catch (Exception x) {
			throw new IllegalArgumentException("Cannot evaluate " + this.expression + " for parameter " + eov,x);
		}
	}
	@Override
	public boolean check(Map bindings) {
		try {
			return (Boolean) MVEL.executeExpression(this.compiledExpression,bindings);
			//return MVEL.evalToBoolean(this.expression,eov);
		}
		catch (Exception x) {
			throw new IllegalArgumentException("Cannot evaluate " + this.expression + " for parameter " + bindings,x);
		}
	}

	@Override
	public String getExpression() {
		return this.expression;
	}

	@Override
	public List<String> getRoles() {
		return this.roleNames;
	}

	@Override
	public String getFirstRole() {
		return this.roleNames.isEmpty()?null:this.roleNames.get(0);
	}

	@Override
	public boolean isSingleRole() {
		return this.roleNames.size()==1;
	}

	@Override
	public String toString() {
		return new StringBuffer()
			.append("mvel:\"")
			.append(this.expression)
			.append("\"")
			.toString();
	}




	
	
}
