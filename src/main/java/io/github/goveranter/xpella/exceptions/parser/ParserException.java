package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.exceptions.XpellaException;
import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public abstract class ParserException extends XpellaException
{
	public ParserException(String message, XpellaParserBookmark bookmark)
	{
		super(message + " (line " + bookmark.getLine() + ", col " + bookmark.getColumn());
	}
}
