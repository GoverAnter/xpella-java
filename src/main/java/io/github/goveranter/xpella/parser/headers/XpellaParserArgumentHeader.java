package io.github.goveranter.xpella.parser.headers;

public class XpellaParserArgumentHeader
{
	private String type;
	private boolean hasDefault;
	
	public XpellaParserArgumentHeader(String type, boolean hasDefault)
	{
		this.type = type;
		this.hasDefault = hasDefault;
	}
	
	public String getType()
	{
		return type;
	}
	public boolean hasDefault()
	{
		return hasDefault;
	}
}
