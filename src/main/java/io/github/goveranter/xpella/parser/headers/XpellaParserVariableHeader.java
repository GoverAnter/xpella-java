package io.github.goveranter.xpella.parser.headers;

import io.github.goveranter.xpella.ast.XpellaASTVariableDeclaration;
import io.github.goveranter.xpella.scopes.ScopeVariable;

import java.util.List;

public class XpellaParserVariableHeader implements ScopeVariable
{
	public static XpellaParserVariableHeader fromDeclaration(XpellaASTVariableDeclaration declaration)
	{
		return new XpellaParserVariableHeader(declaration.getIdentifier(),
				declaration.getType(),
				declaration.getVisibility(),
				declaration.getModifiers());
	}
	
	private String identifier;
	private String type;
	private String visibility;
	private List<String> modifiers;
	
	public XpellaParserVariableHeader(String identifier, String type, String visibility, List<String> modifiers)
	{
		this.identifier = identifier;
		this.type = type;
		this.visibility = visibility;
		this.modifiers = modifiers;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaParserVariableHeader)
		{
			XpellaParserVariableHeader header = (XpellaParserVariableHeader)other;
			if(header.identifier.equals(this.identifier) &&
					header.type.equals(this.type) &&
					header.visibility.equals(this.visibility) && header.modifiers.size() == this.modifiers.size())
			{
				for(int i = 0; i < this.modifiers.size(); i++)
				{
					if(!header.modifiers.get(i).equals(this.modifiers.get(i)))
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
}
