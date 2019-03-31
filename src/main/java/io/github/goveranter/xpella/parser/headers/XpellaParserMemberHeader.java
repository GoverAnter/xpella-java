package io.github.goveranter.xpella.parser.headers;

import io.github.goveranter.xpella.ast.XpellaASTVariableDeclaration;

import java.util.List;

public class XpellaParserMemberHeader extends XpellaParserVariableHeader
{
	public static XpellaParserMemberHeader fromDeclaration(XpellaASTVariableDeclaration declaration)
	{
		return new XpellaParserMemberHeader(declaration.getIdentifier(), declaration.getType(), declaration.getVisibility(), declaration.getModifiers());
	}
	
	public XpellaParserMemberHeader(String identifier, String type, String visibility, List<String> modifiers)
	{
		super(identifier, type, visibility, modifiers);
	}
}
