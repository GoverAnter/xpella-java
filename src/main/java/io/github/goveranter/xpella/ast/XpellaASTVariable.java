package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTVariable extends XpellaASTExpression
{
	private String identifier;
	
	public XpellaASTVariable(String resolvedType, String identifier)
	{
		super(resolvedType);
		this.identifier = identifier;
	}
	
	public XpellaASTVariable(List<XpellaASTAnnotation> annotations, String documentation, String resolvedType, String identifier)
	{
		super(annotations, documentation, resolvedType);
		this.identifier = identifier;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTVariable)
		{
			XpellaASTVariable variable = (XpellaASTVariable)other;
			// Due to runtime type erasure, resolvedType will be null in runtime AST
			return this.identifier.equals(variable.identifier);
		}
		
		return false;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
}
