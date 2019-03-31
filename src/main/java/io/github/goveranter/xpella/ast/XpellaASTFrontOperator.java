package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTFrontOperator extends XpellaASTOperator
{
	private XpellaASTVariable left;
	
	public XpellaASTFrontOperator(String resolvedType, String operator, XpellaASTVariable left)
	{
		super(resolvedType, operator);
		this.left = left;
	}
	
	public XpellaASTFrontOperator(List<XpellaASTAnnotation> annotations, String documentation, String resolvedType, String operator, XpellaASTVariable left)
	{
		super(annotations, documentation, resolvedType, operator);
		this.left = left;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTFrontOperator)
		{
			XpellaASTFrontOperator op = (XpellaASTFrontOperator)other;
			return op.getOperator().equals(this.getOperator()) && op.left.equals(this.left);
		}
		
		return false;
	}
	
	public XpellaASTVariable getLeft()
	{
		return left;
	}
}
