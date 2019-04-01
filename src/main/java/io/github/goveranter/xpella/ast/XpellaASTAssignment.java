package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTAssignment extends XpellaASTStatement
{
	private XpellaASTExpression variable;
	private XpellaASTExpression expression;
	
	public XpellaASTAssignment(XpellaASTExpression variable, XpellaASTExpression expression)
	{
		this.variable = variable;
		this.expression = expression;
	}
	
	public XpellaASTAssignment(List<XpellaASTAnnotation> annotations, String documentation, XpellaASTExpression variable, XpellaASTExpression expression)
	{
		super(annotations, documentation);
		this.variable = variable;
		this.expression = expression;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTAssignment)
		{
			XpellaASTAssignment assignment = (XpellaASTAssignment)other;
			return assignment.variable.equals(this.variable) &&
					(assignment.expression == null || assignment.expression.equals(this.expression)) || this.expression == null;
		}
		
		return false;
	}
	
	public XpellaASTExpression getVariable()
	{
		return variable;
	}
	public XpellaASTExpression getExpression()
	{
		return expression;
	}
}
