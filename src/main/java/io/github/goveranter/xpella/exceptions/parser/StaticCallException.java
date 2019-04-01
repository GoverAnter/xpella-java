package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class StaticCallException extends ParserException
{
	public StaticCallException(String identifier, boolean isMember, XpellaParserBookmark bookmark)
	{
		super("Cannot call non static " + (isMember ? "member" : "method") + " \"" + identifier + "\" without instance", bookmark);
	}
}
