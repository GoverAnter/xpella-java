package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class CannotAssignValueException extends ParserException
{
	public CannotAssignValueException(String errorType, XpellaParserBookmark bookmark)
	{
		super("Cannot assign value to a " + errorType, bookmark);
	}
}
