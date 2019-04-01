package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class NameReservedException extends ParserException
{
	public NameReservedException(String identifier, String type, XpellaParserBookmark bookmark)
	{
		super("\"" + identifier + "\" is a reserved" + (type.isEmpty() ? "" : " " + type) + " keyword", bookmark);
	}
}
