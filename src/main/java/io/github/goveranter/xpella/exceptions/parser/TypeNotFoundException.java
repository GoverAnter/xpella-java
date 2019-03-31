package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class TypeNotFoundException extends ParserException
{
	public TypeNotFoundException(String type, XpellaParserBookmark bookmark)
	{
		super("Cannot find type \"" + type + "\"", bookmark);
	}
}
