package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class TypeCoercionException extends ParserException
{
	public TypeCoercionException(String operator, String opType, String argType, XpellaParserBookmark bookmark)
	{
		super("Operator \"" + operator + "\" from type \"" + opType + "\" cannot accept type \"" + argType + "\"", bookmark);
	}
}
