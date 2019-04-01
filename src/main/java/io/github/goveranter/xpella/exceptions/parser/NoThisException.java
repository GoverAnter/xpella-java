package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class NoThisException extends ParserException
{
	public NoThisException(XpellaParserBookmark bookmark)
	{
		super("No \"this\" is present in this context", bookmark);
	}
}
