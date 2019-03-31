package io.github.goveranter.xpella.ast;

import java.util.List;

public abstract class XpellaASTDeclaration extends XpellaASTStatement
{
	private String identifier;
	
	public XpellaASTDeclaration(String identifier)
	{
		super();
		this.identifier = identifier;
	}
	
	public XpellaASTDeclaration(List<XpellaASTAnnotation> annotations, String documentation, String identifier)
	{
		super(annotations, documentation);
		this.identifier = identifier;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
}
