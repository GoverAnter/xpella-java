package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class UnexpectedCharacterException extends ParserException
{
	public UnexpectedCharacterException(String unexpected, XpellaParserBookmark bookmark)
	{
		super("Unexpected character \"" + unexpected + "\"", bookmark);
	}
	
	public UnexpectedCharacterException(String expected, String unexpected, XpellaParserBookmark bookmark)
	{
		super("Expected \"" + expected + "\", got \"" + unexpected + "\"", bookmark);
	}
}
