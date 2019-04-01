package io.github.goveranter.xpella.parser.helpers;

public class XpellaParserBookmark
{
	private final int position;
	private final int line;
	private final int column;
	
	public XpellaParserBookmark(int position, int line, int column)
	{
		this.position = position;
		this.line = line;
		this.column = column;
	}
	
	public XpellaParserBookmark(XpellaInputStream inputStream)
	{
		this.position = inputStream.getCurrentPosition();
		this.line = inputStream.getCurrentLine();
		this.column = inputStream.getCurrentColumn();
	}
	
	public int getPosition()
	{
		return position;
	}
	public int getLine()
	{
		return line;
	}
	public int getColumn()
	{
		return column;
	}
}
