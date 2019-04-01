package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class NotAFunctionException extends ParserException
{
	public NotAFunctionException(String identifier, String type, XpellaParserBookmark bookmark)
	{
		super("Tying to call \"" + identifier + "\" as a function, but is \"" + type + "\"", bookmark);
	}
}
