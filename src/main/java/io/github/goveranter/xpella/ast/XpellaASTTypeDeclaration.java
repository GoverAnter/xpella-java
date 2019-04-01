package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTTypeDeclaration extends XpellaASTDeclaration
{
	private List<XpellaASTVariableDeclaration> members;
	private List<XpellaASTFunctionDeclaration> methods;
	
	public XpellaASTTypeDeclaration(String identifier, List<XpellaASTVariableDeclaration> members, List<XpellaASTFunctionDeclaration> methods)
	{
		super(identifier);
		this.members = members;
		this.methods = methods;
	}
	
	public XpellaASTTypeDeclaration(List<XpellaASTAnnotation> annotations, String documentation, String identifier, List<XpellaASTVariableDeclaration> members, List<XpellaASTFunctionDeclaration> methods)
	{
		super(annotations, documentation, identifier);
		this.members = members;
		this.methods = methods;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTTypeDeclaration)
		{
			XpellaASTTypeDeclaration decl = (XpellaASTTypeDeclaration)other;
			if(decl.members.size() == this.members.size() && decl.methods.size() == this.methods.size())
			{
				for(int i = 0; i < this.members.size(); i++)
				{
					if(!decl.members.get(i).equals(this.members.get(i)))
						return false;
				}
				for(int i = 0; i < this.methods.size(); i++)
				{
					if(!decl.methods.get(i).equals(this.methods.get(i)))
						return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	public List<XpellaASTVariableDeclaration> getMembers()
	{
		return members;
	}
	public List<XpellaASTFunctionDeclaration> getMethods()
	{
		return methods;
	}
}
