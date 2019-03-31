package io.github.goveranter.xpella.parser.headers;

import java.util.List;

public class XpellaParserOperatorHeader
{
	private boolean front;
	private boolean back;
	private List<String> acceptedTypes;
	
	public XpellaParserOperatorHeader(boolean front, boolean back, List<String> acceptedTypes)
	{
		this.front = front;
		this.back = back;
		this.acceptedTypes = acceptedTypes;
	}
	
	public boolean isFront()
	{
		return front;
	}
	public boolean isBack()
	{
		return back;
	}
	public List<String> getAcceptedTypes()
	{
		return acceptedTypes;
	}
}
