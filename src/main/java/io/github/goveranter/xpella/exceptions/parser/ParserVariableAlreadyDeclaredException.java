package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class ParserVariableAlreadyDeclaredException extends ParserException
{
	public ParserVariableAlreadyDeclaredException(String identifier, XpellaParserBookmark bookmark)
	{
		super("Variable \"" + identifier + "\" is already declared and cannot be shadowed", bookmark);
	}
}
