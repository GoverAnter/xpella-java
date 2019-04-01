package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class VariableAssignmentException extends ParserException
{
	public VariableAssignmentException(String expected, String actual, XpellaParserBookmark bookmark)
	{
		super("Cannot assign expression of type \"" + actual + "\" to variable of type \"" + expected + "\"", bookmark);
	}
}
