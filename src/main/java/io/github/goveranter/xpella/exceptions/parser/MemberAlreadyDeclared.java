package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class MemberAlreadyDeclared extends ParserException
{
	public MemberAlreadyDeclared(String identifier, boolean member, XpellaParserBookmark bookmark)
	{
		super("\"" + identifier + "\" is already declared as a " + (member ? "member" : "method") + " of this class", bookmark);
	}
}
