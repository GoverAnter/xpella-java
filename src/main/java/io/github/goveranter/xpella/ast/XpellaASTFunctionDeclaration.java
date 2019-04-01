package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTFunctionDeclaration extends XpellaASTDeclaration
{
	private String visibility;
	private List<String> modifiers;
	private String returnType;
	private List<XpellaASTVariableDeclaration> args;
	private XpellaASTStatement execution;
	
	public XpellaASTFunctionDeclaration(String identifier,
										String visibility,
										List<String> modifiers,
										String returnType,
										List<XpellaASTVariableDeclaration> args,
										XpellaASTStatement execution)
	{
		super(identifier);
		this.visibility = visibility;
		this.modifiers = modifiers;
		this.returnType = returnType;
		this.args = args;
		this.execution = execution;
	}
	
	public XpellaASTFunctionDeclaration(List<XpellaASTAnnotation> annotations,
										String documentation,
										String identifier,
										String visibility,
										List<String> modifiers,
										String returnType,
										List<XpellaASTVariableDeclaration> args,
										XpellaASTStatement execution)
	{
		super(annotations, documentation, identifier);
		this.visibility = visibility;
		this.modifiers = modifiers;
		this.returnType = returnType;
		this.args = args;
		this.execution = execution;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTFunctionDeclaration)
		{
			XpellaASTFunctionDeclaration decl = (XpellaASTFunctionDeclaration)other;
			if(decl.visibility.equals(this.visibility) &&
					decl.returnType.equals(this.returnType) &&
					decl.execution.equals(this.execution) &&
					decl.modifiers.size() == this.modifiers.size() &&
					decl.args.size() == this.args.size())
			{
				for(int i = 0; i < this.modifiers.size(); i++)
				{
					if(!decl.modifiers.get(i).equals(this.modifiers.get(i)))
						return false;
				}
				for(int i = 0; i < this.args.size(); i++)
				{
					if(!decl.args.get(i).equals(this.args.get(i)))
						return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	public String getVisibility()
	{
		return visibility;
	}
	public List<String> getModifiers()
	{
		return modifiers;
	}
	public String getReturnType()
	{
		return returnType;
	}
	public List<XpellaASTVariableDeclaration> getArgs()
	{
		return args;
	}
	public XpellaASTStatement getExecution()
	{
		return execution;
	}
}
