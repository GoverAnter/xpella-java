package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTVariableDeclaration extends XpellaASTDeclaration
{
	private String type;
	private String visibility;
	private List<String> modifiers;
	private XpellaASTExpression initialAssignation;
	
	public XpellaASTVariableDeclaration(String identifier, String type, String visibility, List<String> modifiers)
	{
		this(identifier, type, visibility, modifiers, new XpellaASTLiteral(null, null));
	}
	
	public XpellaASTVariableDeclaration(List<XpellaASTAnnotation> annotations, String documentation, String identifier, String type, String visibility, List<String> modifiers)
	{
		this(annotations, documentation, identifier, type, visibility, modifiers, new XpellaASTLiteral(null, null));
	}
	
	public XpellaASTVariableDeclaration(String identifier, String type, String visibility, List<String> modifiers, XpellaASTExpression initialAssignation)
	{
		super(identifier);
		this.type = type;
		this.visibility = visibility;
		this.modifiers = modifiers;
		this.initialAssignation = initialAssignation;
	}
	
	public XpellaASTVariableDeclaration(List<XpellaASTAnnotation> annotations, String documentation, String identifier, String type, String visibility, List<String> modifiers, XpellaASTExpression initialAssignation)
	{
		super(annotations, documentation, identifier);
		this.type = type;
		this.visibility = visibility;
		this.modifiers = modifiers;
		this.initialAssignation = initialAssignation;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTVariableDeclaration)
		{
			XpellaASTVariableDeclaration decl = (XpellaASTVariableDeclaration)other;
			
			if(decl.getIdentifier().equals(this.getIdentifier()) &&
					decl.type.equals(this.type) &&
					decl.visibility.equals(this.visibility) &&
					decl.initialAssignation.equals(this.initialAssignation) &&
					decl.modifiers.size() == this.modifiers.size())
			{
				for(int i = 0; i < decl.modifiers.size(); i++)
				{
					if(!decl.modifiers.get(i).equals(this.modifiers.get(i)))
						return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	public String getType()
	{
		return type;
	}
	public String getVisibility()
	{
		return visibility;
	}
	public List<String> getModifiers()
	{
		return modifiers;
	}
	public XpellaASTExpression getInitialAssignation()
	{
		return initialAssignation;
	}
}
