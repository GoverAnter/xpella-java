package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTBackOperator extends XpellaASTOperator
{
	private XpellaASTVariable right;
	
	public XpellaASTBackOperator(String resolvedType, String operator, XpellaASTVariable right)
	{
		super(resolvedType, operator);
		this.right = right;
	}
	
	public XpellaASTBackOperator(List<XpellaASTAnnotation> annotations, String documentation, String resolvedType, String operator, XpellaASTVariable right)
	{
		super(annotations, documentation, resolvedType, operator);
		this.right = right;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTBackOperator)
		{
			XpellaASTBackOperator op = (XpellaASTBackOperator)other;
			return op.getOperator().equals(this.getOperator()) && op.right.equals(this.right);
		}
		
		return false;
	}
	
	public XpellaASTVariable getRight()
	{
		return right;
	}
}
