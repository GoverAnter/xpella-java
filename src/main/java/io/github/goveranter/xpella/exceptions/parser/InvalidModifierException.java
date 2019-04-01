package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class InvalidModifierException extends ParserException
{
	public InvalidModifierException(String modifier, XpellaParserBookmark bookmark)
	{
		super("\"" + modifier + "\" is not a valid modifier", bookmark);
	}
}
