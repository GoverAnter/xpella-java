package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTReturnStatement extends XpellaASTStatement
{
	private XpellaASTExpression expression;
	
	public XpellaASTReturnStatement(XpellaASTExpression expression)
	{
		super();
		this.expression = expression;
	}
	
	public XpellaASTReturnStatement(List<XpellaASTAnnotation> annotations, String documentation, XpellaASTExpression expression)
	{
		super(annotations, documentation);
		this.expression = expression;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTReturnStatement)
		{
			XpellaASTReturnStatement ret = (XpellaASTReturnStatement)other;
			return (ret.expression != null && ret.expression.equals(this.expression)) ||
					this.expression == null;
		}
		
		return false;
	}
	
	public XpellaASTExpression getExpression()
	{
		return expression;
	}
}
