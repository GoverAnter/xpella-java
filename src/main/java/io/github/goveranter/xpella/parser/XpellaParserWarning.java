package io.github.goveranter.xpella.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class XpellaParserWarning
{
	private String message;
	private XpellaParserBookmark bookmark;
	
	public XpellaParserWarning(String message, XpellaParserBookmark bookmark)
	{
		this.message = message;
		this.bookmark = bookmark;
	}
	
	public String getMessage()
	{
		return message;
	}
	public XpellaParserBookmark getBookmark()
	{
		return bookmark;
	}
}
