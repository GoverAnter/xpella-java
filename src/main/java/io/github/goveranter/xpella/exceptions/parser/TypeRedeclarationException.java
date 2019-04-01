package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class TypeRedeclarationException extends ParserException
{
	public TypeRedeclarationException(String type, XpellaParserBookmark bookmark)
	{
		super("Type \"" + type + "\" is already declared", bookmark);
	}
}
