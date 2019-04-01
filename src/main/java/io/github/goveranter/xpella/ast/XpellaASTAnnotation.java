package io.github.goveranter.xpella.ast;

import java.util.ArrayList;
import java.util.List;

public class XpellaASTAnnotation
{
	protected String identifier;
	protected List<XpellaASTLiteral> args;
	protected boolean runtimeAnnotation;
	
	public XpellaASTAnnotation(String identifier) {
		this(identifier, new ArrayList<>(), true);
	}
	
	/** Provided args MUST ABSOLUTELY BE LITERALS */
	public XpellaASTAnnotation(String identifier, List<XpellaASTLiteral> args, boolean runtimeAnnotation) {
		this.identifier = identifier;
		this.args = args;
		this.runtimeAnnotation = runtimeAnnotation;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
	public List<XpellaASTLiteral> getArgs()
	{
		return args;
	}
	public boolean isRuntimeAnnotation()
	{
		return runtimeAnnotation;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTAnnotation)
		{
			XpellaASTAnnotation annotation = (XpellaASTAnnotation)other;
			
			if(annotation.identifier.equals(this.identifier) && annotation.args.size() == this.args.size())
			{
				for(int i = 0; i < annotation.args.size(); i++)
				{
					if(!annotation.args.get(i).equals(this.args.get(i)))
						return false;
				}
				return true;
			}
		}
		
		return false;
	}
}
