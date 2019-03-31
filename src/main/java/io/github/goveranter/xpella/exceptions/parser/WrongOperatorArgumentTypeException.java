package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class WrongOperatorArgumentTypeException extends ParserException
{
	public WrongOperatorArgumentTypeException(String operator, String opType, String argType, XpellaParserBookmark bookmark)
	{
		super("Operator \"" + operator + "\" from type \"" + opType + "\" cannot accept type \"" + argType + "\"", bookmark);
	}
}
