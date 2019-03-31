package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class UnmetExpectationException extends ParserException
{
	public UnmetExpectationException(String message, XpellaParserBookmark bookmark)
	{
		super(message, bookmark);
	}
}
