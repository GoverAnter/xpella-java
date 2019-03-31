package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTFunctionCall extends XpellaASTExpression
{
	private String identifier;
	private List<XpellaASTExpression> args;
	
	public XpellaASTFunctionCall(String resolvedType, String identifier, List<XpellaASTExpression> args)
	{
		super(resolvedType);
		this.identifier = identifier;
		this.args = args;
	}
	
	public XpellaASTFunctionCall(List<XpellaASTAnnotation> annotations, String documentation, String resolvedType, String identifier, List<XpellaASTExpression> args)
	{
		super(annotations, documentation, resolvedType);
		this.identifier = identifier;
		this.args = args;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTFunctionCall)
		{
			XpellaASTFunctionCall call = (XpellaASTFunctionCall)other;
			if(call.identifier.equals(this.identifier) && call.args.size() == this.args.size())
			{
				for(int i = 0; i < this.args.size(); i++)
				{
					if(!call.args.get(i).equals(this.args.get(i)))
						return false;
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
	public List<XpellaASTExpression> getArgs()
	{
		return args;
	}
}
