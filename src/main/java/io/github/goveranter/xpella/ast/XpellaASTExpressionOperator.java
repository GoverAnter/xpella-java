package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTExpressionOperator extends XpellaASTOperator
{
	private XpellaASTExpression left;
	private XpellaASTExpression right;
	
	public XpellaASTExpressionOperator(String resolvedType, String operator, XpellaASTExpression left, XpellaASTExpression right)
	{
		super(resolvedType, operator);
		this.left = left;
		this.right = right;
	}
	
	public XpellaASTExpressionOperator(List<XpellaASTAnnotation> annotations, String documentation, String resolvedType, String operator, XpellaASTExpression left, XpellaASTExpression right)
	{
		super(annotations, documentation, resolvedType, operator);
		this.left = left;
		this.right = right;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTExpressionOperator)
		{
			XpellaASTExpressionOperator op = (XpellaASTExpressionOperator)other;
			return op.getOperator().equals(this.getOperator()) &&
					op.left.equals(this.left) &&
					op.right.equals(this.right);
		}
		
		return false;
	}
	
	public XpellaASTExpression getLeft()
	{
		return left;
	}
	public XpellaASTExpression getRight()
	{
		return right;
	}
}
