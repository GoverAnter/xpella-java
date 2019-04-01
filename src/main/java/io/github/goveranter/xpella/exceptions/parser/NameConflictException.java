package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class NameConflictException extends ParserException
{
	public NameConflictException(String identifier, XpellaParserBookmark bookmark)
	{
		super("\"" + identifier + "\" is a declared type, name conflicts with type object", bookmark);
	}
}
