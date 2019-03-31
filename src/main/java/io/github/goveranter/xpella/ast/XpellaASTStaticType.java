package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTStaticType extends XpellaASTExpression
{
	public XpellaASTStaticType(String type)
	{
		super(type);
	}
	
	public XpellaASTStaticType(List<XpellaASTAnnotation> annotations, String documentation, String type)
	{
		super(annotations, documentation, type);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTStaticType)
		{
			// resolvedType can't be null here
			return ((XpellaASTStaticType)other).getResolvedType().equals(this.getResolvedType());
		}
		
		return false;
	}
}
