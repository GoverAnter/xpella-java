package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class MultipleVisibilityModifiersException extends ParserException
{
	public MultipleVisibilityModifiersException(XpellaParserBookmark bookmark)
	{
		super("Having more than one visibility modifier is forbidden", bookmark);
	}
}
