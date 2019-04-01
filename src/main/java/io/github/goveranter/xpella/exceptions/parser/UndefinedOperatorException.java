package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class UndefinedOperatorException extends ParserException
{
	public UndefinedOperatorException(String message, XpellaParserBookmark bookmark)
	{
		super(message, bookmark);
	}
}
