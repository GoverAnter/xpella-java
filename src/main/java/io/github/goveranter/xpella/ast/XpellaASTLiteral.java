package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTLiteral extends XpellaASTExpression
{
	private Object value;
	
	public XpellaASTLiteral(String resolvedType, Object value)
	{
		super(resolvedType);
		this.value = value;
	}
	
	public XpellaASTLiteral(List<XpellaASTAnnotation> annotations, String documentation, String resolvedType, Object value)
	{
		super(annotations, documentation, resolvedType);
		this.value = value;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTLiteral)
		{
			XpellaASTLiteral lit = (XpellaASTLiteral)other;
			return lit.getResolvedType().equals(this.getResolvedType()) && lit.value.equals(this.value);
		}
		
		return false;
	}
	
	public Object getValue()
	{
		return value;
	}
}
