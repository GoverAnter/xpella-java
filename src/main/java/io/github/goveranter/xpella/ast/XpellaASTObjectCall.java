package io.github.goveranter.xpella.ast;

import java.util.List;

public abstract class XpellaASTObjectCall extends XpellaASTExpression
{
	private XpellaASTExpression object;
	
	public XpellaASTObjectCall(String resolvedType, XpellaASTExpression object)
	{
		super(resolvedType);
		this.object = object;
	}
	
	public XpellaASTObjectCall(List<XpellaASTAnnotation> annotations, String documentation, String resolvedType, XpellaASTExpression object)
	{
		super(annotations, documentation, resolvedType);
		this.object = object;
	}
	
	public XpellaASTExpression getObject()
	{
		return object;
	}
}
