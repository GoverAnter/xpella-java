package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class MemberNotFoundException extends ParserException
{
	public MemberNotFoundException(String member, String type, XpellaParserBookmark bookmark)
	{
		super("Cannot find member \"" + member + "\" in type \"" + type + "\"", bookmark);
	}
}
