package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class DefaultArgumentOrderException extends ParserException
{
	public DefaultArgumentOrderException(XpellaParserBookmark bookmark)
	{
		super("Cannot have a non default parameter after a default one", bookmark);
	}
}
