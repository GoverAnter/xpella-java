package io.github.goveranter.xpella.ast;

import java.util.List;

public abstract class XpellaASTExpression extends XpellaASTStatement
{
	private String resolvedType;
	
	public XpellaASTExpression(String resolvedType)
	{
		super();
		this.resolvedType = resolvedType;
	}
	
	public XpellaASTExpression(List<XpellaASTAnnotation> annotations, String documentation, String resolvedType)
	{
		super(annotations, documentation);
		this.resolvedType = resolvedType;
	}
	
	public String getResolvedType()
	{
		return resolvedType;
	}
	public void setResolvedType(String resolvedType)
	{
		this.resolvedType = resolvedType;
	}
	
	@Override
	public boolean isRuntimeAST()
	{
		return this.resolvedType == null || super.isRuntimeAST();
	}
}
