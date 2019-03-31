package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class NoDeclaredOperatorException extends ParserException
{
	public NoDeclaredOperatorException(String type, String operator, String operatorType, XpellaParserBookmark bookmark)
	{
		super("Type \"" + type + "\" does not declare a " + operatorType + " \"" + operator + "\"", bookmark);
	}
}
