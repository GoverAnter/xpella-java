package io.github.goveranter.xpella.ast;

import java.util.List;

public abstract class XpellaASTOperator extends XpellaASTExpression
{
	private String operator;
	
	public XpellaASTOperator(String resolvedType, String operator)
	{
		super(resolvedType);
		this.operator = operator;
	}
	
	public XpellaASTOperator(List<XpellaASTAnnotation> annotations, String documentation, String resolvedType, String operator)
	{
		super(annotations, documentation, resolvedType);
		this.operator = operator;
	}
	
	public String getOperator()
	{
		return operator;
	}
}
