package io.github.goveranter.xpella.parser.headers;

import io.github.goveranter.xpella.ast.XpellaASTFunctionDeclaration;

import java.util.List;
import java.util.stream.Collectors;

public class XpellaParserFunctionHeader extends XpellaParserVariableHeader
{
	public static XpellaParserFunctionHeader fromDeclaration(XpellaASTFunctionDeclaration declaration)
	{
		return new XpellaParserFunctionHeader(
				declaration.getIdentifier(),
				declaration.getReturnType(),
				declaration.getVisibility(),
				declaration.getModifiers(),
				declaration.getArgs().stream()
						.map(arg -> new XpellaParserArgumentHeader(arg.getType(), arg.getInitialAssignation() != null))
						.collect(Collectors.toList()));
	}
	
	private String returnType;
	/** The array of the types to provide as arguments */
	private List<XpellaParserArgumentHeader> args;
	
	public XpellaParserFunctionHeader(String identifier, String returnType, String visibility, List<String> modifiers, List<XpellaParserArgumentHeader> args)
	{
		super(identifier, "function", visibility, modifiers);
		this.returnType = returnType;
		this.args = args;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(super.equals(other) && other instanceof XpellaParserFunctionHeader)
		{
			XpellaParserFunctionHeader header = (XpellaParserFunctionHeader)other;
			if(header.returnType.equals(this.returnType) && header.args.size() == this.args.size())
			{
				for(int i = 0; i < this.args.size(); i++)
				{
					if(!header.args.get(i).equals(this.args.get(i)))
						return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	public String getReturnType()
	{
		return returnType;
	}
	public List<XpellaParserArgumentHeader> getArgs()
	{
		return args;
	}
}
